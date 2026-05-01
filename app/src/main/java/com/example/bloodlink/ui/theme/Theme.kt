package com.example.bloodlink.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


private val AppColorScheme = lightColorScheme(
    primary = PrimaryRed,
    onPrimary = Color.White,
    background = Color.White,
    surface = Color.White,
    onBackground = TextDark,
    onSurface = TextDark
)

@Composable
fun BloodLinkTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = AppTypography,
        content = content
    )
}
