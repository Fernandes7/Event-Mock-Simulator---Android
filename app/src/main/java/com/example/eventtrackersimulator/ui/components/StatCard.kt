package com.example.eventtrackersimulator.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eventtrackersimulator.domain.EventType
import com.example.eventtrackersimulator.ui.theme.MantineBlue
import com.example.eventtrackersimulator.ui.theme.MantineBlueDark
import com.example.eventtrackersimulator.ui.theme.MantineBlueLight
import com.example.eventtrackersimulator.ui.theme.MantineGray2
import com.example.eventtrackersimulator.ui.theme.MantineGray6
import com.example.eventtrackersimulator.ui.theme.MantineGray9

@Composable
fun HeroStatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MantineBlueLight),
        modifier = modifier,
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = label.uppercase(),
                color = MantineBlueDark,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp,
            )
            Spacer(Modifier.height(8.dp))
            Text(text = value, color = MantineBlue, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun MetricCard(eventType: EventType, label: String, value: Int, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, MantineGray2),
        modifier = modifier,
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                EventTypeDot(eventType)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = label.uppercase(),
                    color = MantineGray6,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp,
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(text = value.toString(), color = MantineGray9, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
    }
}
