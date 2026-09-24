package com.example.eventtrackersimulator.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    /** Name of [com.example.eventtrackersimulator.domain.EventType], stored as text. */
    val eventType: String,

    /** JSON-encoded payload map (see [com.example.eventtrackersimulator.common.JsonUtils]). */
    val payload: String,

    /** Epoch millis captured the moment the event entered the queue. */
    val timestamp: Long,

    /** The app-process session UUID active when the event entered the queue. */
    val sessionId: String,

    /** Name of [com.example.eventtrackersimulator.domain.EventStatus], stored as text. */
    val status: String,

    /** How many times ingestion has failed and been retried for this event. */
    val retryCount: Int = 0,

    /**
     * Epoch millis of the next scheduled ingestion attempt while status == RETRYING.
     * Null unless the event is currently retrying. Populated by the ingestion loop
     * (see IngestionWorker TODO) purely so the UI can render a "Retrying in Ns" countdown.
     */
    val nextAttemptAt: Long? = null,
)
