# Ad Attribution Tracker

An Android app-event-tracking **simulator**. It exposes a small SDK-style API
(`EventTracker.track()` / `trackBatch()`) for recording ad-attribution events
(`INSTALL`, `VISIT`, `PURCHASE`, `ADD_TO_CART`), persists them durably in a local
Room database, and simulates a real ingestion pipeline in the background —
random network delay, a random success/failure roll, and automatic retries —
using WorkManager. Two Compose/Material3 screens let you watch it happen:
an **Event Queue** (live feed + retry list) and **Statistics** (aggregate counts).

## Tech stack

| Concern | Library |
|---|---|
| Language | Kotlin 2.2 |
| UI | Jetpack Compose + Material3 |
| Async | Kotlin Coroutines + Flow |
| Local persistence | Room (via KSP for annotation processing) |
| Background work | WorkManager (`CoroutineWorker`) |
| DI | None — a single `Application` subclass wires the few singletons this app needs |
| Navigation | None — two flat screens switched by a bottom `NavigationBar` + a `when` |
| JSON | `org.json` (Android's built-in JSON classes, for `trackBatch`) |

No icon library or downloadable fonts are used — the couple of icons in the UI
(refresh button, bottom-nav icons) are plain Unicode glyphs, and text uses the
system font, to avoid pulling in extra dependencies for a handful of glyphs.

## How an event flows through the app, end to end

```
 caller                 EventTracker              Room ("events" table)            IngestionWorker (WorkManager)          UI (Compose)
   │                         │                              │                                  │                              │
   │ track(VISIT, payload)   │                              │                                  │                              │
   ├────────────────────────>│                              │                                  │                              │
   │                         │ stamp timestamp + sessionId  │                                  │                              │
   │                         │ insert, status = QUEUED      │                                  │                              │
   │                         ├─────────────────────────────>│                                  │                              │
   │                         │                              │<─────────── observeByStatuses ───┼─── Flow<List<EventEntity>> ─>│ (recomposes live)
   │                         │                              │                                  │                              │
   │                         │                              │<── getPendingEvents() ───────────┤                              │
   │                         │                              │                                  │ mark PROCESSING              │
   │                         │                              │<── delay 1-5s, roll 80/20 ───────┤                              │
   │                         │                              │<── mark PROCESSED / RETRYING ────┤                              │
   │                         │                              │                                  │ re-enqueue next pass         │
   │                         │                              │                                  │ (loops forever)              │
```

1. **Something calls `EventTracker.track(eventType, payload)`** (or `trackBatch(jsonArrayString)`
   for several events at once, e.g. `[{"eventType":"VISIT","payload":{"screen":"home"}}]`).
   In this scaffold, that "something" is a demo **`+`** FAB on the Event Queue screen — see
   [Known limitations](#known-limitations--whats-left) below.
2. **`EventTracker` stamps and inserts it.** It attaches `System.currentTimeMillis()` and the
   current `sessionId` (a UUID generated once when the app process starts), builds an
   `EventEntity` with `status = QUEUED`, and writes it to Room. This is a suspend Room call, so
   the event is durable the instant it returns — no in-memory-only state, nothing is lost if the
   process dies right after.
3. **The UI is just a live query over the same table.** Both screens' ViewModels observe
   `Flow<List<EventEntity>>` / `Flow<Int>` queries from `EventDao`. Room automatically re-emits
   those flows whenever the `events` table changes, so inserts/updates show up without any
   manual refresh plumbing.
4. **`IngestionWorker` (a WorkManager `CoroutineWorker`) does the actual "ingestion":** each pass
   it reads every `QUEUED`/`RETRYING` row, and for each one:
   - marks it `PROCESSING`,
   - waits a random delay between 1–5 seconds (`Constants.INGESTION_MIN_DELAY_MS`/`MAX_DELAY_MS`),
   - rolls a random outcome — **80%** success, **20%** failure (`Constants.INGESTION_SUCCESS_RATE`),
   - on success, marks it `PROCESSED` (permanent),
   - on failure, marks it `RETRYING` (bumps `retryCount`, and sets `nextAttemptAt` so the UI can
     show a "Retrying in Ns" countdown).

   There's no terminal failure state — a failed event just goes back to `RETRYING` and gets
   picked up again on a later pass, forever, until it eventually succeeds.
5. **The worker keeps itself running.** At the end of every pass it re-enqueues itself (roughly
   1 second later, `Constants.INGESTION_POLL_INTERVAL_MS`) as unique WorkManager work, so it
   polls continuously for as long as the process is alive. `EventTrackerApp.onCreate()` calls
   `IngestionWorker.enqueue()` on every process start, which is a no-op if a chain is already
   running/scheduled and otherwise (re)starts it — this is what makes the loop resume after the
   app was closed and reopened. If the process is killed *mid-attempt* (an event stuck at
   `PROCESSING`), the next pass resets it back to `QUEUED` before doing anything else, so it
   doesn't get lost in limbo.

## Event lifecycle

```
   QUEUED ──picked up──> PROCESSING ──success (80%)──> PROCESSED   (terminal)
                              │
                              └──failure (20%)──> RETRYING ──picked up again──> PROCESSING (loops)
```

## Screens

### Event Queue
- Shared header (app icon, "Ad Attribution Tracker", screen subtitle) + a refresh button.
- Two tabs:
  - **In Progress** — every event, any status, oldest first (a live activity feed).
  - **Failed / Retrying** — only events currently in `RETRYING`.
- Pull-to-refresh (cosmetic — Room's Flow is already live, this just gives the gesture its
  expected spinner beat).
- A loading spinner is shown until Room's first emission arrives, so "still loading" is never
  confused with "genuinely empty".
- A blue **`+`** FAB — a developer aid that queues one random sample event, since nothing else
  in this scaffold generates events on its own (see below).

### Statistics
- Two hero cards: **Total Events Processed** and **Total Visits (Unique Session)**.
- A 2×2 grid of per-event-type processed counts (INSTALL / VISIT / ADD_TO_CART / PURCHASE).
- An **Event Breakdown** table: count + percentage of total, per event type.

All numbers are plain Room aggregate queries (`COUNT`, `COUNT(DISTINCT sessionId)`,
`GROUP BY eventType`) over rows where `status = 'PROCESSED'` — no business logic, just reads.

### Theme
A fixed Mantine (mantine.dev) blue palette (`ui/theme/Color.kt`) — no dark theme or dynamic
color, since the design calls for fixed brand colors. 12dp corner radius on cards (Mantine's
default), reused as Material3's "medium" shape so most cards get it for free.

## Project structure

```
app/src/main/java/com/example/eventtrackersimulator/
├── EventTrackerApp.kt          Application: wires DB → repository → EventTracker, starts the worker
├── MainActivity.kt             Hosts the Compose tree
├── common/                     Cross-cutting helpers, no business logic
│   ├── Constants.kt              Prefs keys, WorkManager name, ingestion delay/success-rate tuning
│   ├── DateTimeUtils.kt           Timestamp formatting, retry countdown math
│   └── JsonUtils.kt               org.json helpers for payload maps and trackBatch parsing
├── domain/
│   ├── EventType.kt              INSTALL / VISIT / PURCHASE / ADD_TO_CART
│   ├── EventStatus.kt            QUEUED / PROCESSING / RETRYING / PROCESSED
│   └── EventTracker.kt           Public track()/trackBatch() API, stamps timestamp + sessionId
├── data/
│   ├── local/                    Room: EventEntity, EventDao, AppDatabase, EventTypeCount
│   ├── prefs/AppPrefs.kt          SharedPreferences (install-tracked flag storage)
│   └── repository/EventRepository.kt   Thin pass-through over EventDao/AppPrefs
├── worker/IngestionWorker.kt    The background ingestion/retry simulation loop
└── ui/
    ├── theme/                     Mantine color tokens, Material3 theme, typography
    ├── components/                AppHeader, EventTypeDot, StatusPill, HeroStatCard/MetricCard
    ├── queue/                     EventQueueScreen + its ViewModel
    ├── statistics/                StatisticsScreen + its ViewModel
    └── AppRoot.kt                 Bottom nav + screen switching + ViewModel wiring
```

## Known limitations / what's left

- **Dedup rules aren't enforced yet.** The original spec calls for INSTALL to be processed only
  once ever (persisted, survives restarts) and VISIT only once per `sessionId`, with no limit on
  PURCHASE/ADD_TO_CART. `EventTracker.trackInternal()` currently queues every event
  unconditionally. `AppPrefs` (install-tracked flag) and `EventRepository.countTrackedForSession()`
  already exist to support this — they're just not wired up in `EventTracker` yet.
- **No real event source.** Nothing in the app calls `track()`/`trackBatch()` except the `+` FAB
  on the Event Queue screen, which exists purely so the pipeline has something to show. A real
  app would call `EventTracker.track(...)` from wherever installs/visits/purchases actually happen.

## Building & running

```bash
./gradlew :app:assembleDebug
```

Notes:
- Requires network access on first build (Room/WorkManager/KSP artifacts aren't vendored).
- `gradle.properties` sets `android.disallowKotlinSourceSets=false`. This project uses AGP's
  built-in Kotlin compilation (no separate `kotlin-android` plugin), and KSP (for Room's codegen)
  still registers generated sources through the older `kotlin.sourceSets` DSL — this flag is the
  official suppression AGP's own error points to for that specific combination.
- minSdk 24, targetSdk/compileSdk 37.
