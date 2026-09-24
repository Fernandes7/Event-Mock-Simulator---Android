package com.example.eventtrackersimulator.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {

    /** Persists a newly-tracked event with status = QUEUED. Returns the generated row id. */
    @Insert
    suspend fun insert(event: EventEntity): Long

    /** Live feed of events whose status is one of [statuses], oldest first (as in the mockup). */
    @Query("SELECT * FROM events WHERE status IN (:statuses) ORDER BY timestamp ASC")
    fun observeByStatuses(statuses: List<String>): Flow<List<EventEntity>>

    /** One-shot read of everything still waiting to be ingested, oldest first (FIFO). */
    @Query("SELECT * FROM events WHERE status IN ('QUEUED', 'RETRYING') ORDER BY timestamp ASC")
    suspend fun getPendingEvents(): List<EventEntity>

    @Query("UPDATE events SET status = 'PROCESSING' WHERE id = :id")
    suspend fun markProcessing(id: Long)

    @Query("UPDATE events SET status = 'PROCESSED', nextAttemptAt = NULL WHERE id = :id")
    suspend fun markProcessed(id: Long)

    @Query(
        "UPDATE events SET status = 'RETRYING', retryCount = retryCount + 1, nextAttemptAt = :nextAttemptAt " +
            "WHERE id = :id",
    )
    suspend fun markRetrying(id: Long, nextAttemptAt: Long)

    /** How many events of [eventType] already exist for [sessionId], regardless of status. */
    @Query("SELECT COUNT(*) FROM events WHERE eventType = :eventType AND sessionId = :sessionId")
    suspend fun countByTypeAndSession(eventType: String, sessionId: String): Int

    /** Total events that have completed ingestion. */
    @Query("SELECT COUNT(*) FROM events WHERE status = 'PROCESSED'")
    fun observeTotalProcessed(): Flow<Int>

    /** Unique sessions that produced at least one processed VISIT event. */
    @Query("SELECT COUNT(DISTINCT sessionId) FROM events WHERE eventType = 'VISIT' AND status = 'PROCESSED'")
    fun observeUniqueVisitSessions(): Flow<Int>

    /** Processed-event counts grouped by type, used for both the metric cards and the breakdown table. */
    @Query("SELECT eventType, COUNT(*) as count FROM events WHERE status = 'PROCESSED' GROUP BY eventType")
    fun observeBreakdown(): Flow<List<EventTypeCount>>
}
