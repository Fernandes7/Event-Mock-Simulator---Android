package com.example.eventtrackersimulator.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import android.widget.Toast
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.eventtrackersimulator.EventTrackerApp
import com.example.eventtrackersimulator.domain.EventType
import com.example.eventtrackersimulator.ui.queue.EventQueueScreen
import com.example.eventtrackersimulator.ui.queue.EventQueueViewModel
import com.example.eventtrackersimulator.ui.statistics.StatisticsScreen
import com.example.eventtrackersimulator.ui.statistics.StatisticsViewModel
import com.example.eventtrackersimulator.ui.theme.MantineGray6
import com.example.eventtrackersimulator.ui.theme.MantineGray9
import com.example.eventtrackersimulator.ui.theme.MantineOrange

/**
 * The two top-level destinations. No navigation-compose here -- just a bottom bar switching
 * which screen is composed, which is all two flat (non-nested) screens need.
 */
private enum class AppTab(val label: String, val glyph: String) {
    QUEUE("Queue", "☰"),
    STATISTICS("Statistics", "📊"),
}

@Composable
fun AppRoot() {
    val context = LocalContext.current
    val app = context.applicationContext as EventTrackerApp
    var selectedTab by rememberSaveable { mutableStateOf(AppTab.QUEUE) }

    LaunchedEffect(app.eventTracker) {
        app.eventTracker.dedupDropped.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    val queueViewModel: EventQueueViewModel = viewModel(
        factory = viewModelFactory { initializer { EventQueueViewModel(app.repository) } },
    )
    val inProgress by queueViewModel.inProgress.collectAsState()
    val failedRetrying by queueViewModel.failedRetrying.collectAsState()
    val queueBadgeCount = (inProgress?.size ?: 0) + (failedRetrying?.size ?: 0)

    Scaffold(
        bottomBar = {
            BottomNavBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                queueBadgeCount = queueBadgeCount,
            )
        },
    ) { innerPadding ->
        when (selectedTab) {
            AppTab.QUEUE -> {
                EventQueueScreen(
                    queueViewModel,
                    modifier = Modifier.padding(innerPadding),
                    onAddSampleEvent = { app.eventTracker.track(EventType.entries.random()) },
                )
            }

            AppTab.STATISTICS -> {
                val viewModel: StatisticsViewModel = viewModel(
                    factory = viewModelFactory { initializer { StatisticsViewModel(app.repository) } },
                )
                StatisticsScreen(viewModel, modifier = Modifier.padding(innerPadding))
            }
        }
    }
}

/**
 * Custom pill-shaped bottom bar: the selected tab renders as a solid dark pill (icon+label in
 * white), the other tab stays plain gray text -- not the standard Material3 NavigationBar look.
 */
@Composable
private fun BottomNavBar(selectedTab: AppTab, onTabSelected: (AppTab) -> Unit, queueBadgeCount: Int) {
    Surface(color = Color.White, shadowElevation = 8.dp, shape = RoundedCornerShape(28.dp), modifier = Modifier.padding(16.dp)) {
        Row(Modifier.padding(6.dp)) {
            AppTab.entries.forEach { tab ->
                NavPillItem(
                    selected = selectedTab == tab,
                    label = tab.label,
                    glyph = tab.glyph,
                    badgeCount = if (tab == AppTab.QUEUE) queueBadgeCount else null,
                    onClick = { onTabSelected(tab) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun NavPillItem(
    selected: Boolean,
    label: String,
    glyph: String,
    badgeCount: Int?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = if (selected) MantineGray9 else Color.Transparent
    val contentColor = if (selected) Color.White else MantineGray6

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = glyph, color = contentColor, fontSize = 16.sp)
        Spacer(Modifier.width(6.dp))
        Text(text = label, color = contentColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        if (badgeCount != null && badgeCount > 0) {
            Spacer(Modifier.width(6.dp))
            Box(
                modifier = Modifier.size(24.dp).clip(CircleShape).background(MantineOrange),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = badgeCount.toString(), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
