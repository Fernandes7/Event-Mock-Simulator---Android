package com.example.eventtrackersimulator.ui.queue

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eventtrackersimulator.common.DateTimeUtils
import com.example.eventtrackersimulator.data.local.EventEntity
import com.example.eventtrackersimulator.domain.EventStatus
import com.example.eventtrackersimulator.domain.EventType
import com.example.eventtrackersimulator.ui.components.AppHeader
import com.example.eventtrackersimulator.ui.components.EventTypeIconChip
import com.example.eventtrackersimulator.ui.components.HeaderIconButton
import com.example.eventtrackersimulator.ui.components.StatusPill
import com.example.eventtrackersimulator.ui.theme.MantineBlue
import com.example.eventtrackersimulator.ui.theme.MantineGray0
import com.example.eventtrackersimulator.ui.theme.MantineGray2
import com.example.eventtrackersimulator.ui.theme.MantineGray6
import com.example.eventtrackersimulator.ui.theme.MantineGray9
import com.example.eventtrackersimulator.ui.theme.MantineRed

private val TABS = listOf("All", "In progress", "Retrying")
private val TAB_BADGE_COLORS = listOf(MantineGray6, MantineBlue, MantineRed)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventQueueScreen(
    viewModel: EventQueueViewModel,
    modifier: Modifier = Modifier,
    onAddSampleEvent: () -> Unit = {},
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val all by viewModel.all.collectAsState()
    val inProgress by viewModel.inProgress.collectAsState()
    val failedRetrying by viewModel.failedRetrying.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    Box(modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            AppHeader(
                subtitle = "Event Queue",
                trailingAction = {
                    HeaderIconButton(icon = "↻", contentDescription = "Refresh", onClick = viewModel::refresh)
                },
            )

            QueueTabRow(
                selectedTab = selectedTab,
                counts = listOf(all?.size ?: 0, inProgress?.size ?: 0, failedRetrying?.size ?: 0),
                onTabSelected = { selectedTab = it },
            )

            val events = when (selectedTab) {
                0 -> all
                1 -> inProgress
                else -> failedRetrying
            }

            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = viewModel::refresh,
                modifier = Modifier.weight(1f),
            ) {
                when {
                    events == null -> LoadingState()
                    events.isEmpty() -> EmptyQueueState()
                    else -> LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        items(events, key = { it.id }) { event -> EventCard(event) }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = onAddSampleEvent,
            containerColor = MantineBlue,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp),
        ) {
            Text(text = "+", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun QueueTabRow(selectedTab: Int, counts: List<Int>, onTabSelected: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MantineGray0)
            .padding(4.dp),
    ) {
        TABS.forEachIndexed { index, label ->
            val selected = selectedTab == index
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (selected) Color.White else Color.Transparent)
                    .clickable { onTabSelected(index) }
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = label,
                    color = if (selected) MantineGray9 else MantineGray6,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 14.sp,
                )
                Spacer(Modifier.width(6.dp))
                CountBadge(count = counts[index], color = TAB_BADGE_COLORS[index])
            }
        }
    }
}

@Composable
private fun CountBadge(count: Int, color: Color) {
    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Text(text = count.toString(), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
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
            EventTypeIconChip(eventType)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(text = eventType.name, color = MantineGray9, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(text = DateTimeUtils.formatTime(event.timestamp), color = MantineGray6, fontSize = 12.sp)
            }
            Spacer(Modifier.width(8.dp))
            StatusPill(status = status)
        }
    }
}

@Composable
private fun LoadingState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = MantineBlue)
    }
}

@Composable
private fun EmptyQueueState() {
    Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
        Text(text = "No events here yet.", color = MantineGray6, fontSize = 14.sp)
    }
}
