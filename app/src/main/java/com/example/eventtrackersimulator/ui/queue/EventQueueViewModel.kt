package com.example.eventtrackersimulator.ui.queue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventtrackersimulator.data.local.EventEntity
import com.example.eventtrackersimulator.data.repository.EventRepository
import com.example.eventtrackersimulator.domain.EventStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class EventQueueViewModel(repository: EventRepository) : ViewModel() {

    val all: StateFlow<List<EventEntity>?> =
        repository.observeByStatuses(EventStatus.entries)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    // Only unresolved work -- PROCESSED events drop off this one once they're done.
    val inProgress: StateFlow<List<EventEntity>?> =
        repository.observeByStatuses(listOf(EventStatus.QUEUED, EventStatus.PROCESSING))
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val failedRetrying: StateFlow<List<EventEntity>?> =
        repository.observeByStatuses(listOf(EventStatus.RETRYING))
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            delay(400.milliseconds)
            _isRefreshing.value = false
        }
    }
}
