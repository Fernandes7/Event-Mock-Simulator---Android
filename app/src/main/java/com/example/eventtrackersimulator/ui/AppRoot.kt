package com.example.eventtrackersimulator.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.example.eventtrackersimulator.ui.theme.MantineBlue
import com.example.eventtrackersimulator.ui.theme.MantineGray6

/**
 * The two top-level destinations. No navigation-compose here -- just a bottom bar switching
 * which screen is composed, which is all two flat (non-nested) screens need.
 */
private enum class AppTab(val label: String, val glyph: String) {
    QUEUE("Queue", "☰"),
    STATISTICS("Statistics", "📊")
}

@Composable
fun AppRoot() {
    val app = LocalContext.current.applicationContext as EventTrackerApp
    var selectedTab by rememberSaveable { mutableStateOf(AppTab.QUEUE) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                AppTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        icon = { Text(text = tab.glyph, fontSize = 20.sp) },
                        label = { Text(tab.label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MantineBlue,
                            selectedTextColor = MantineBlue,
                            unselectedIconColor = MantineGray6,
                            unselectedTextColor = MantineGray6,
                            indicatorColor = Color.Transparent,
                        ),
                    )
                }
            }
        },
    ) { innerPadding ->
        when (selectedTab) {
            AppTab.QUEUE -> {
                val viewModel: EventQueueViewModel = viewModel(
                    factory = viewModelFactory { initializer { EventQueueViewModel(app.repository) } },
                )
                EventQueueScreen(
                    viewModel,
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
