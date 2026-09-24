package com.example.eventtrackersimulator.ui.queue

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eventtrackersimulator.common.DateTimeUtils
import com.example.eventtrackersimulator.data.local.EventEntity
import com.example.eventtrackersimulator.domain.EventStatus
import com.example.eventtrackersimulator.domain.EventType
import com.example.eventtrackersimulator.ui.components.AppHeader
import com.example.eventtrackersimulator.ui.components.EventTypeDot
import com.example.eventtrackersimulator.ui.components.HeaderIconButton
import com.example.eventtrackersimulator.ui.components.StatusPill
import com.example.eventtrackersimulator.ui.theme.MantineBlue
import com.example.eventtrackersimulator.ui.theme.MantineGray2
import com.example.eventtrackersimulator.ui.theme.MantineGray6
import com.example.eventtrackersimulator.ui.theme.MantineGray9

private val TABS = listOf("IN PROGRESS", "FAILED / RETRYING")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventQueueScreen(viewModel: EventQueueViewModel, modifier: Modifier = Modifier) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val inProgress by viewModel.inProgress.collectAsState()
    val failedRetrying by viewModel.failedRetrying.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    Column(modifier.fillMaxSize()) {
        AppHeader(
            subtitle = "Event Queue",
            trailingAction = {
                HeaderIconButton(icon = "↻", contentDescription = "Refresh", onClick = viewModel::refresh)
            },
        )

        TabRow(selectedTabIndex = selectedTab, containerColor = Color.White, contentColor = MantineBlue) {
            TABS.forEachIndexed { index, label ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = label,
                            fontSize = 13.sp,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                        )
                    },
                )
            }
        }

        val events = if (selectedTab == 0) inProgress else failedRetrying

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = viewModel::refresh,
            modifier = Modifier.weight(1f),
        ) {
            if (events.isEmpty()) {
                EmptyQueueState()
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(events, key = { it.id }) { event -> EventCard(event) }
                }
            }
        }
    }
}

@Composable
private fun EventCard(event: EventEntity, modifier: Modifier = Modifier) {
    val eventType = remember(event.eventType) { EventType.valueOf(event.eventType) }
    val status = remember(event.status) { EventStatus.valueOf(event.status) }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, MantineGray2),
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            EventTypeDot(eventType)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(text = eventType.name, color = MantineGray9, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(text = DateTimeUtils.formatTime(event.timestamp), color = MantineGray6, fontSize = 12.sp)
            }
            Spacer(Modifier.width(8.dp))
            StatusPill(status = status, nextAttemptAt = event.nextAttemptAt)
        }
    }
}

@Composable
private fun EmptyQueueState() {
    Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
        Text(text = "No events here yet.", color = MantineGray6, fontSize = 14.sp)
    }
}
