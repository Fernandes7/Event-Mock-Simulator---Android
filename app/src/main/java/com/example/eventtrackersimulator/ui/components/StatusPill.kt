package com.example.eventtrackersimulator.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eventtrackersimulator.common.DateTimeUtils
import com.example.eventtrackersimulator.domain.EventStatus
import com.example.eventtrackersimulator.ui.theme.MantineBlue
import com.example.eventtrackersimulator.ui.theme.MantineBlueLight
import com.example.eventtrackersimulator.ui.theme.MantineGreen
import com.example.eventtrackersimulator.ui.theme.MantineRed
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun StatusPill(status: EventStatus, nextAttemptAt: Long? = null, modifier: Modifier = Modifier) {
    when (status) {
        EventStatus.PROCESSED ->
            PillChip(MantineGreen.copy(alpha = 0.15f), MantineGreen, "Processed ✓", modifier)
        EventStatus.QUEUED, EventStatus.PROCESSING ->
            PillChip(MantineBlueLight, MantineBlue, "In progress", modifier)
        EventStatus.RETRYING ->
            RetryingPillChip(nextAttemptAt, modifier)
    }
}

@Composable
private fun PillChip(containerColor: Color, contentColor: Color, label: String, modifier: Modifier) {
    Surface(color = containerColor, shape = RoundedCornerShape(50), modifier = modifier) {
        Text(
            text = label,
            color = contentColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
        )
    }
}

@Composable
private fun RetryingPillChip(nextAttemptAt: Long?, modifier: Modifier) {
    var secondsLeft by remember(nextAttemptAt) {
        mutableLongStateOf(nextAttemptAt?.let { DateTimeUtils.secondsUntil(it) } ?: 0L)
    }
    LaunchedEffect(nextAttemptAt) {
        while (nextAttemptAt != null && secondsLeft > 0) {
            delay(1_000.milliseconds)
            secondsLeft = DateTimeUtils.secondsUntil(nextAttemptAt)
        }
    }
    val label = if (nextAttemptAt == null) "Retrying" else "Retrying in ${secondsLeft}s"
    PillChip(MantineRed.copy(alpha = 0.12f), MantineRed, label, modifier)
}
