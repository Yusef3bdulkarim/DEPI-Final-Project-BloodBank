package com.example.depi_final_project_bloodbank.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = MaroonPrimary,
    onPrimary = MaroonOnPrimary,
    primaryContainer = MaroonContainer,
    secondary = DarkNavy,
    background = BackgroundLight,
    surface = Color.White,
    onSurface = DarkNavy,
    error = UrgentRed,
    tertiary = SuccessGreen
)

@Composable
fun DepiFinalProjectBloodBankTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
