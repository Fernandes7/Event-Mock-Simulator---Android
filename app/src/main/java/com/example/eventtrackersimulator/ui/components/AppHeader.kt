package com.example.eventtrackersimulator.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eventtrackersimulator.ui.theme.MantineBlue
import com.example.eventtrackersimulator.ui.theme.MantineGray2
import com.example.eventtrackersimulator.ui.theme.MantineGray6
import com.example.eventtrackersimulator.ui.theme.MantineGray9


@Composable
fun AppHeader(
    subtitle: String,
    modifier: Modifier = Modifier,
    trailingAction: (@Composable () -> Unit)? = null,
) {
    Column(modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MantineBlue),
                contentAlignment = Alignment.Center,
            ) {
                Text("A", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = "Ad Attribution Tracker",
                    color = MantineGray9,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                )
                Text(text = subtitle, color = MantineGray6, fontSize = 13.sp)
            }
            trailingAction?.invoke()
        }
    }
}


@Composable
fun HeaderIconButton(icon: String, contentDescription: String, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Color.White)
            .border(1.dp, MantineGray2, CircleShape)
            .semantics { this.contentDescription = contentDescription },
    ) {
        Text(text = icon, color = MantineGray9, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}
