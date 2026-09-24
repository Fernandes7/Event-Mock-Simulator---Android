package com.example.eventtrackersimulator.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.example.eventtrackersimulator.domain.EventType
data class BarSegment(val eventType: EventType, val percentage: Float)
@Composable
fun SegmentedProgressBar(segments: List<BarSegment>, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(Color.White.copy(alpha = 0.2f)),
    ) {
        segments.filter { it.percentage > 0f }.forEach { segment ->
            Row(
                modifier = Modifier
                    .weight(segment.percentage)
                    .fillMaxHeight()
                    .background(segment.eventType.dotColor()),
            ) {}
        }
    }
}
