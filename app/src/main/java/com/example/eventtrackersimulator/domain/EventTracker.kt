package com.example.eventtrackersimulator.domain

import com.example.eventtrackersimulator.common.JsonUtils.parseEventBatch
import com.example.eventtrackersimulator.common.JsonUtils.toJsonString
import com.example.eventtrackersimulator.data.local.EventEntity
import com.example.eventtrackersimulator.data.repository.EventRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.UUID

class EventTracker(
    private val repository: EventRepository,
    private val scope: CoroutineScope,
) {

    val sessionId: String = UUID.randomUUID().toString()
    private val dedupMutex = Mutex()
    private var visitTrackedThisSession = false

    private val _dedupDropped = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val dedupDropped: SharedFlow<String> = _dedupDropped.asSharedFlow()

    fun track(eventType: EventType, payload: Map<String, String> = emptyMap()) {
        scope.launch { trackInternal(eventType, payload) }
    }

    fun trackBatch(jsonArrayString: String) {
        scope.launch {
            parseEventBatch(jsonArrayString).forEach { item ->
                trackInternal(EventType.valueOf(item.eventType), item.payload)
            }
        }
    }

    private suspend fun trackInternal(eventType: EventType, payload: Map<String, String>) = dedupMutex.withLock {
        when (eventType) {
            // INSTALL: process only once ever.
            EventType.INSTALL -> if (repository.appPrefs.isInstallTracked()) {
                _dedupDropped.tryEmit("INSTALL already recorded -- skipped")
                return@withLock
            }
            // VISIT: process only once per session.
            EventType.VISIT -> if (visitTrackedThisSession) {
                _dedupDropped.tryEmit("VISIT already recorded for this session -- skipped")
                return@withLock
            }
            // PURCHASE / ADD_TO_CART: no dedup limit.
            EventType.PURCHASE, EventType.ADD_TO_CART -> Unit
        }

        val entity = EventEntity(
            eventType = eventType.name,
            payload = payload.toJsonString(),
            timestamp = System.currentTimeMillis(),
            sessionId = sessionId,
            status = EventStatus.QUEUED.name,
        )
        repository.enqueueEvent(entity)

        // Only mark as tracked once the event is actually queued.
        when (eventType) {
            EventType.INSTALL -> repository.appPrefs.setInstallTracked(true)
            EventType.VISIT -> visitTrackedThisSession = true
            EventType.PURCHASE, EventType.ADD_TO_CART -> Unit
        }
    }
}
