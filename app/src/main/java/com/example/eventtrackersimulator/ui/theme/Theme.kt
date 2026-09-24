package com.example.eventtrackersimulator.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val MantineColorScheme = lightColorScheme(
    primary = MantineBlue,
    onPrimary = Color.White,
    primaryContainer = MantineBlueLight,
    onPrimaryContainer = MantineBlueDark,
    secondary = MantineViolet,
    onSecondary = Color.White,
    tertiary = MantineOrange,
    onTertiary = Color.White,
    background = MantineGray0,
    onBackground = MantineGray9,
    surface = Color.White,
    onSurface = MantineGray9,
    surfaceVariant = MantineGray2,
    onSurfaceVariant = MantineGray6,
    outline = MantineGray2,
)

private val MantineShapes = Shapes(
    medium = RoundedCornerShape(MantineRadiusMd),
    large = RoundedCornerShape(MantineRadiusMd),
)

@Composable
fun EventTrackerSimulatorTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = MantineColorScheme,
        typography = Typography,
        shapes = MantineShapes,
        content = content
    )
}