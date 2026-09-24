package com.example.eventtrackersimulator.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.eventtrackersimulator.EventTrackerApp
import com.example.eventtrackersimulator.common.Constants
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

class IngestionWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val repository = (applicationContext as EventTrackerApp).repository

        // Recover anything left stuck at PROCESSING by a previous run that got killed mid-attempt.
        repository.resetStuckProcessingEvents()

        for (event in repository.getPendingEvents()) {
            repository.markProcessing(event.id)

            val simulatedDelayMs = Random.nextLong(Constants.INGESTION_MIN_DELAY_MS, Constants.INGESTION_MAX_DELAY_MS)
            delay(simulatedDelayMs.milliseconds)

            val didSucceed = Random.nextFloat() < Constants.INGESTION_SUCCESS_RATE
            if (didSucceed) {
                repository.markProcessed(event.id)
            } else {
                repository.markRetrying(event.id, nextAttemptAt = System.currentTimeMillis() + simulatedDelayMs)
            }
        }

        scheduleNextPass(applicationContext)
        return Result.success()
    }

    companion object {
        /** Unique work name so at most one ingestion chain is ever enqueued at a time. */
        const val UNIQUE_WORK_NAME = Constants.INGESTION_WORK_NAME

        /**
         * Starts the ingestion loop, or leaves it alone if a chain is already running/scheduled.
         * Safe to call on every app process start (see [EventTrackerApp.onCreate]).
         */
        fun enqueue(context: Context) {
            WorkManager.getInstance(context)
                .enqueueUniqueWork(UNIQUE_WORK_NAME, ExistingWorkPolicy.KEEP, buildRequest())
        }

        /**
         * Called by the worker itself, from inside [doWork], to schedule the next pass. Uses
         * REPLACE (not KEEP) because the currently-running pass still counts as "existing work"
         * until this call returns -- REPLACE swaps it out for the next scheduled pass instead
         * of being ignored.
         */
        private fun scheduleNextPass(context: Context) {
            WorkManager.getInstance(context)
                .enqueueUniqueWork(UNIQUE_WORK_NAME, ExistingWorkPolicy.REPLACE, buildRequest())
        }

        private fun buildRequest() = OneTimeWorkRequestBuilder<IngestionWorker>()
            .setInitialDelay(Constants.INGESTION_POLL_INTERVAL_MS, TimeUnit.MILLISECONDS)
            .build()
    }
}
