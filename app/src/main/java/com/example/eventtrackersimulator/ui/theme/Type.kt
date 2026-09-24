package com.example.eventtrackersimulator.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.eventtrackersimulator.R

val FigtreeFontFamily = FontFamily(
    Font(R.font.bhim_sdk_figtree_light, FontWeight.Light),
    Font(R.font.bhim_sdk_figtree_regular, FontWeight.Normal),
    Font(R.font.bhim_sdk_figtree_medium, FontWeight.Medium),
)

private val defaultTypography = Typography()

val Typography = Typography(
    displayLarge = defaultTypography.displayLarge.copy(fontFamily = FigtreeFontFamily),
    displayMedium = defaultTypography.displayMedium.copy(fontFamily = FigtreeFontFamily),
    displaySmall = defaultTypography.displaySmall.copy(fontFamily = FigtreeFontFamily),
    headlineLarge = defaultTypography.headlineLarge.copy(fontFamily = FigtreeFontFamily),
    headlineMedium = defaultTypography.headlineMedium.copy(fontFamily = FigtreeFontFamily),
    headlineSmall = defaultTypography.headlineSmall.copy(fontFamily = FigtreeFontFamily),
    titleLarge = defaultTypography.titleLarge.copy(fontFamily = FigtreeFontFamily),
    titleMedium = defaultTypography.titleMedium.copy(fontFamily = FigtreeFontFamily),
    titleSmall = defaultTypography.titleSmall.copy(fontFamily = FigtreeFontFamily),
    bodyLarge = defaultTypography.bodyLarge.copy(fontFamily = FigtreeFontFamily),
    bodyMedium = defaultTypography.bodyMedium.copy(fontFamily = FigtreeFontFamily),
    bodySmall = defaultTypography.bodySmall.copy(fontFamily = FigtreeFontFamily),
    labelLarge = defaultTypography.labelLarge.copy(fontFamily = FigtreeFontFamily),
    labelMedium = defaultTypography.labelMedium.copy(fontFamily = FigtreeFontFamily),
    labelSmall = defaultTypography.labelSmall.copy(fontFamily = FigtreeFontFamily),
)
