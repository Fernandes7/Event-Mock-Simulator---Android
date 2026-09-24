package com.example.eventtrackersimulator.ui.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventtrackersimulator.data.repository.EventRepository
import com.example.eventtrackersimulator.domain.EventType
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class BreakdownRow(val eventType: EventType, val count: Int, val percentage: Float)

data class StatisticsUiState(
    val totalProcessed: Int = 0,
    val uniqueVisitSessions: Int = 0,
    val countsByType: Map<EventType, Int> = EventType.entries.associateWith { 0 },
    val breakdown: List<BreakdownRow> = emptyList(),
)

class StatisticsViewModel(repository: EventRepository) : ViewModel() {

    val uiState: StateFlow<StatisticsUiState> = combine(
        repository.observeTotalProcessed(),
        repository.observeUniqueVisitSessions(),
        repository.observeBreakdown(),
    ) { totalProcessed, uniqueVisitSessions, breakdownCounts ->
        val countsByType = EventType.entries.associateWith { type ->
            breakdownCounts.firstOrNull { it.eventType == type.name }?.count ?: 0
        }
        val breakdown = EventType.entries.map { type ->
            val count = countsByType.getValue(type)
            val percentage = if (totalProcessed > 0) count * 100f / totalProcessed else 0f
            BreakdownRow(type, count, percentage)
        }
        StatisticsUiState(totalProcessed, uniqueVisitSessions, countsByType, breakdown)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), StatisticsUiState())
}
