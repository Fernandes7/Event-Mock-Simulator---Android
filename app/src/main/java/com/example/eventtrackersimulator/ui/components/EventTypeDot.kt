package com.example.eventtrackersimulator.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.eventtrackersimulator.domain.EventType
import com.example.eventtrackersimulator.ui.theme.MantineBlue
import com.example.eventtrackersimulator.ui.theme.MantineGreen
import com.example.eventtrackersimulator.ui.theme.MantineOrange
import com.example.eventtrackersimulator.ui.theme.MantineViolet

fun EventType.dotColor(): Color = when (this) {
    EventType.INSTALL -> MantineGreen
    EventType.VISIT -> MantineBlue
    EventType.ADD_TO_CART -> MantineViolet
    EventType.PURCHASE -> MantineOrange
}

@Composable
fun EventTypeDot(eventType: EventType, modifier: Modifier = Modifier, size: Dp = 10.dp) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(eventType.dotColor()),
    )
}
