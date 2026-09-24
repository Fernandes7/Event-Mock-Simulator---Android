package com.example.eventtrackersimulator.domain

import com.example.eventtrackersimulator.common.JsonUtils.parseEventBatch
import com.example.eventtrackersimulator.common.JsonUtils.toJsonString
import com.example.eventtrackersimulator.data.local.EventEntity
import com.example.eventtrackersimulator.data.repository.EventRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.UUID

class EventTracker(
    private val repository: EventRepository,
    private val scope: CoroutineScope,
) {

    val sessionId: String = UUID.randomUUID().toString()

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

    private suspend fun trackInternal(eventType: EventType, payload: Map<String, String>) {
        val entity = EventEntity(
            eventType = eventType.name,
            payload = payload.toJsonString(),
            timestamp = System.currentTimeMillis(),
            sessionId = sessionId,
            status = EventStatus.QUEUED.name,
        )
        repository.enqueueEvent(entity)
    }
}
