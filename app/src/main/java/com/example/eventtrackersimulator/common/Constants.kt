package com.example.eventtrackersimulator.common


object Constants {
    const val DATABASE_NAME = "event_tracker.db"
    const val PREFS_NAME = "event_tracker_prefs"
    const val PREF_KEY_INSTALL_TRACKED = "install_tracked"
    const val INGESTION_WORK_NAME = "ingestion_worker"
    const val INGESTION_MIN_DELAY_MS = 1_000L
    const val INGESTION_MAX_DELAY_MS = 5_000L
    const val INGESTION_SUCCESS_RATE = 0.8f
}
