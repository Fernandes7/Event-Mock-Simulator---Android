package com.example.eventtrackersimulator

import android.app.Application
import com.example.eventtrackersimulator.data.local.AppDatabase
import com.example.eventtrackersimulator.data.prefs.AppPrefs
import com.example.eventtrackersimulator.data.repository.EventRepository
import com.example.eventtrackersimulator.domain.EventTracker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class EventTrackerApp : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    lateinit var repository: EventRepository
        private set

    lateinit var eventTracker: EventTracker
        private set

    override fun onCreate() {
        super.onCreate()

        val database = AppDatabase.getInstance(this)
        repository = EventRepository(eventDao = database.eventDao(), appPrefs = AppPrefs(this))
        eventTracker = EventTracker(repository, applicationScope)
    }
}
