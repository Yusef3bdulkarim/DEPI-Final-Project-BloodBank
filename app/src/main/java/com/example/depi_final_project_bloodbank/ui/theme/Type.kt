package com.example.depi_final_project_bloodbank.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.depi_final_project_bloodbank.R

val CairoFont = FontFamily(
    Font(R.font.cairo_light, FontWeight.Light),
    Font(R.font.cairo_regular, FontWeight.Normal),
    Font(R.font.cairo_medium, FontWeight.Medium),
    Font(R.font.cairo_semibold, FontWeight.SemiBold),
    Font(R.font.cairo_bold, FontWeight.Bold),
    Font(R.font.cairo_extrabold, FontWeight.ExtraBold),
    Font(R.font.cairo_black, FontWeight.Black)
)

val Typography = Typography(

    titleLarge = TextStyle(
        fontFamily = CairoFont,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 26.sp,
    ),

    titleMedium = TextStyle(
        fontFamily = CairoFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),

    bodyLarge = TextStyle(
        fontFamily = CairoFont,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 18.sp,
    ),

    labelSmall = TextStyle(
        fontFamily = CairoFont,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
    )
)