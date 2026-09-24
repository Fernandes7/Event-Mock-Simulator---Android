package com.example.eventtrackersimulator.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.eventtrackersimulator.EventTrackerApp
import com.example.eventtrackersimulator.common.Constants

/**
 * Simulates ingesting queued events one at a time: a random delay, then a random
 * success/failure roll. This is the piece the user asked to implement themselves --
 * see the TODO in [doWork] for the exact behaviour it needs to have.
 */
class IngestionWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val repository = (applicationContext as EventTrackerApp).repository

        // TODO(ingestion-simulation): Implement the retry/ingestion simulation loop:
        //  1. Read pending work with `repository.getPendingEvents()` (status QUEUED or RETRYING).
        //  2. For each event, mark it PROCESSING (`repository.markProcessing(id)`), then
        //     `delay(Random.nextLong(Constants.INGESTION_MIN_DELAY_MS, Constants.INGESTION_MAX_DELAY_MS))`
        //     to simulate the network round trip.
        //  3. Roll the outcome: `Random.nextFloat() < Constants.INGESTION_SUCCESS_RATE` = success.
        //     - Success -> `repository.markProcessed(id)` (permanent).
        //     - Failure -> `repository.markRetrying(id, nextAttemptAt = System.currentTimeMillis() + <delay>)`.
        //       There is no terminal failure state: it must keep retrying until it succeeds.
        //  4. Re-enqueue this worker so ingestion keeps running continuously, including across
        //     app restarts -- e.g. a unique `OneTimeWorkRequest` for [UNIQUE_WORK_NAME] via
        //     `enqueue(applicationContext)` below, or WorkManager's own `Result.retry()` backoff.
        //     [EventTrackerApp] should call `enqueue()` once on process start so a killed/finished
        //     chain resumes.

        return Result.success()
    }

    companion object {
        /** Unique work name so at most one ingestion chain is ever enqueued at a time. */
        const val UNIQUE_WORK_NAME = Constants.INGESTION_WORK_NAME

        /**
         * TODO(ingestion-simulation): Enqueue this worker as unique work (e.g.
         * `WorkManager.getInstance(context).enqueueUniqueWork(UNIQUE_WORK_NAME, ExistingWorkPolicy.KEEP, request)`)
         * so calling this from [EventTrackerApp.onCreate] both starts the loop on first launch
         * and safely resumes it on every later app open without duplicating work.
         */
        fun enqueue(context: Context) {
        }
    }
}
