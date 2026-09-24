package com.example.eventtrackersimulator.ui.statistics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eventtrackersimulator.ui.components.AppHeader
import com.example.eventtrackersimulator.ui.components.EventTypeDot
import com.example.eventtrackersimulator.ui.components.HeroStatCard
import com.example.eventtrackersimulator.ui.components.MetricCard
import com.example.eventtrackersimulator.ui.theme.MantineGray2
import com.example.eventtrackersimulator.ui.theme.MantineGray6
import com.example.eventtrackersimulator.ui.theme.MantineGray9
import java.util.Locale

@Composable
fun StatisticsScreen(viewModel: StatisticsViewModel, modifier: Modifier = Modifier) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier.fillMaxSize()) {
        AppHeader(subtitle = "Statistics")

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                HeroStatCard(
                    label = "Total Events Processed",
                    value = uiState.totalProcessed.toString(),
                    modifier = Modifier.weight(1f),
                )
                HeroStatCard(
                    label = "Total Visits (Unique Session)",
                    value = uiState.uniqueVisitSessions.toString(),
                    modifier = Modifier.weight(1f),
                )
            }

            uiState.breakdown.chunked(2).forEach { rowPair ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    rowPair.forEach { row ->
                        MetricCard(
                            eventType = row.eventType,
                            label = row.eventType.name.replace('_', ' '),
                            value = row.count,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }

            EventBreakdownCard(rows = uiState.breakdown)
        }
    }
}

@Composable
private fun EventBreakdownCard(rows: List<BreakdownRow>, modifier: Modifier = Modifier) {
    Card(colors = CardDefaults.cardColors(containerColor = Color.White), modifier = modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(text = "Event Breakdown", color = MantineGray9, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(Modifier.height(12.dp))

            Row(Modifier.fillMaxWidth()) {
                Text(
                    text = "EVENT",
                    color = MantineGray6,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = "COUNT",
                    color = MantineGray6,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.End,
                    modifier = Modifier.width(56.dp),
                )
                Text(
                    text = "PERCENTAGE",
                    color = MantineGray6,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.End,
                    modifier = Modifier.width(90.dp),
                )
            }
            HorizontalDivider(color = MantineGray2, modifier = Modifier.padding(vertical = 8.dp))

            rows.forEachIndexed { index, row ->
                BreakdownTableRow(row)
                if (index != rows.lastIndex) {
                    HorizontalDivider(color = MantineGray2)
                }
            }
        }
    }
}

@Composable
private fun BreakdownTableRow(row: BreakdownRow) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            EventTypeDot(row.eventType)
            Spacer(Modifier.width(8.dp))
            Text(text = row.eventType.name, color = MantineGray9, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
        Text(
            text = row.count.toString(),
            color = MantineGray9,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            textAlign = TextAlign.End,
            modifier = Modifier.width(56.dp),
        )
        Text(
            text = String.format(Locale.US, "%.2f%%", row.percentage),
            color = MantineGray6,
            fontSize = 14.sp,
            textAlign = TextAlign.End,
            modifier = Modifier.width(90.dp),
        )
    }
}
