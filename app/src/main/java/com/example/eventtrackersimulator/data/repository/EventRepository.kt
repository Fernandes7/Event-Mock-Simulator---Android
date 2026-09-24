package com.example.eventtrackersimulator.data.repository

import com.example.eventtrackersimulator.data.local.EventDao
import com.example.eventtrackersimulator.data.local.EventEntity
import com.example.eventtrackersimulator.data.local.EventTypeCount
import com.example.eventtrackersimulator.data.prefs.AppPrefs
import com.example.eventtrackersimulator.domain.EventStatus
import kotlinx.coroutines.flow.Flow

class EventRepository(
    private val eventDao: EventDao,
    val appPrefs: AppPrefs,
) {
    suspend fun enqueueEvent(event: EventEntity): Long = eventDao.insert(event)

    fun observeByStatuses(statuses: List<EventStatus>): Flow<List<EventEntity>> =
        eventDao.observeByStatuses(statuses.map { it.name })

    suspend fun getPendingEvents(): List<EventEntity> = eventDao.getPendingEvents()

    suspend fun markProcessing(id: Long) = eventDao.markProcessing(id)

    suspend fun markProcessed(id: Long) = eventDao.markProcessed(id)

    suspend fun markRetrying(id: Long, nextAttemptAt: Long) = eventDao.markRetrying(id, nextAttemptAt)

    suspend fun countTrackedForSession(eventType: String, sessionId: String): Int =
        eventDao.countByTypeAndSession(eventType, sessionId)

    fun observeTotalProcessed(): Flow<Int> = eventDao.observeTotalProcessed()

    fun observeUniqueVisitSessions(): Flow<Int> = eventDao.observeUniqueVisitSessions()

    fun observeBreakdown(): Flow<List<EventTypeCount>> = eventDao.observeBreakdown()
}
