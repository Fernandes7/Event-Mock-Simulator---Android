package com.example.eventtrackersimulator.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eventtrackersimulator.domain.EventStatus
import com.example.eventtrackersimulator.ui.theme.MantineBlue
import com.example.eventtrackersimulator.ui.theme.MantineBlueLight
import com.example.eventtrackersimulator.ui.theme.MantineGreen
import com.example.eventtrackersimulator.ui.theme.MantineRed

@Composable
fun StatusPill(status: EventStatus, modifier: Modifier = Modifier) {
    when (status) {
        EventStatus.PROCESSED ->
            PillChip(MantineGreen.copy(alpha = 0.15f), MantineGreen, "Processed ✓", modifier)
        EventStatus.QUEUED, EventStatus.PROCESSING ->
            PillChip(MantineBlueLight, MantineBlue, "In progress", modifier)
        EventStatus.RETRYING ->
            PillChip(MantineRed.copy(alpha = 0.12f), MantineRed, "Retrying", modifier)
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
