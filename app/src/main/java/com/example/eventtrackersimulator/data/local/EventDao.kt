package com.example.eventtrackersimulator.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {

    @Insert
    suspend fun insert(event: EventEntity): Long
    @Query("SELECT * FROM events WHERE status IN (:statuses) ORDER BY timestamp ASC")
    fun observeByStatuses(statuses: List<String>): Flow<List<EventEntity>>
    @Query("SELECT * FROM events WHERE status IN ('QUEUED', 'RETRYING') ORDER BY timestamp ASC")
    suspend fun getPendingEvents(): List<EventEntity>
    @Query("UPDATE events SET status = 'QUEUED' WHERE status = 'PROCESSING'")
    suspend fun resetStuckProcessingEvents()
    @Query("UPDATE events SET status = 'PROCESSING' WHERE id = :id")
    suspend fun markProcessing(id: Long)
    @Query("UPDATE events SET status = 'PROCESSED', nextAttemptAt = NULL WHERE id = :id")
    suspend fun markProcessed(id: Long)
    @Query(
        "UPDATE events SET status = 'RETRYING', retryCount = retryCount + 1, nextAttemptAt = :nextAttemptAt " +
            "WHERE id = :id",
    )
    suspend fun markRetrying(id: Long, nextAttemptAt: Long)
    @Query("SELECT COUNT(*) FROM events WHERE eventType = :eventType AND sessionId = :sessionId")
    suspend fun countByTypeAndSession(eventType: String, sessionId: String): Int
    @Query("SELECT COUNT(*) FROM events WHERE status = 'PROCESSED'")
    fun observeTotalProcessed(): Flow<Int>
    @Query("SELECT COUNT(DISTINCT sessionId) FROM events WHERE eventType = 'VISIT' AND status = 'PROCESSED'")
    fun observeUniqueVisitSessions(): Flow<Int>
    @Query("SELECT eventType, COUNT(*) as count FROM events WHERE status = 'PROCESSED' GROUP BY eventType")
    fun observeBreakdown(): Flow<List<EventTypeCount>>
}
