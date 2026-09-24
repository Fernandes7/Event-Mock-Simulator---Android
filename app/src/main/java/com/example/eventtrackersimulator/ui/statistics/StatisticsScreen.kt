package com.example.eventtrackersimulator.ui.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eventtrackersimulator.R
import com.example.eventtrackersimulator.ui.components.AppHeader
import com.example.eventtrackersimulator.ui.components.BarSegment
import com.example.eventtrackersimulator.ui.components.EventTypeDot
import com.example.eventtrackersimulator.ui.components.MetricCard
import com.example.eventtrackersimulator.ui.components.SegmentedProgressBar
import com.example.eventtrackersimulator.ui.theme.MantineBlue
import com.example.eventtrackersimulator.ui.theme.MantineBlueDark
import com.example.eventtrackersimulator.ui.theme.MantineGray2
import com.example.eventtrackersimulator.ui.theme.MantineGray6
import com.example.eventtrackersimulator.ui.theme.MantineGray9
import com.example.eventtrackersimulator.ui.theme.MantineRadiusMd
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
            HeroSummaryCard(uiState)

            uiState.breakdown.chunked(2).forEach { rowPair ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    rowPair.forEach { row ->
                        MetricCard(
                            eventType = row.eventType,
                            label = row.eventType.name.replace('_', ' '),
                            value = row.count,
                            percentage = row.percentage,
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
private fun HeroSummaryCard(uiState: StatisticsUiState, modifier: Modifier = Modifier) {
    val topShare = uiState.breakdown.maxByOrNull { it.percentage }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(MantineRadiusMd))
            .background(Brush.linearGradient(listOf(MantineBlue, MantineBlueDark)))
            .padding(20.dp),
    ) {
        Row(Modifier.height(IntrinsicSize.Min)) {
            HeroStat(
                label = stringResource(R.string.total_events_processed),
                value = uiState.totalProcessed.toString(),
                modifier = Modifier.weight(1f),
            )
            Spacer(
                Modifier
                    .fillMaxHeight()
                    .width(1.dp)
                    .background(Color.White.copy(alpha = 0.3f)),
            )
            HeroStat(
                label = stringResource(R.string.total_visits_unique_session),
                value = uiState.uniqueVisitSessions.toString(),
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp),
            )
        }
        Spacer(Modifier.height(20.dp))
        SegmentedProgressBar(
            segments = uiState.breakdown.map { BarSegment(it.eventType, it.percentage) },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
        )

        if (topShare != null && topShare.percentage > 0f) {
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                val label = topShare.eventType.name.lowercase().replace('_', ' ')
                    .replaceFirstChar { it.uppercase() }
                Text(text = "$label share", color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp)
                Text(
                    text = "${topShare.percentage.toInt()}% of events",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                )
            }
        }
    }
}

@Composable
private fun HeroStat(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier) {
        Text(
            text = label.uppercase(),
            color = Color.White.copy(alpha = 0.85f),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.5.sp,
        )
        Spacer(Modifier.height(6.dp))
        Text(text = value, color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun EventBreakdownCard(rows: List<BreakdownRow>, modifier: Modifier = Modifier) {
    Card(colors = CardDefaults.cardColors(containerColor = Color.White), modifier = modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Event Breakdown", color = MantineGray9, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = "Today", color = MantineGray6, fontSize = 13.sp)
            }
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
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
