package com.example.depi_final_project_bloodbank.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.bloodlink.R
val NotoSansArabic = FontFamily(
    Font(R.font.noto_regular, FontWeight.Normal),
    Font(R.font.noto_bold, FontWeight.Bold),
    Font(R.font.noto_semibold, FontWeight.SemiBold),
    Font(R.font.noto_light, FontWeight.Light)
)
val Typography = Typography(
    // العناوين الكبيرة - طلب تبرع عاجل، تم تبرعك
    titleLarge = TextStyle(
        fontFamily = NotoSansArabic,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 26.sp,
    ),
    // عناوين متوسطة - التنبيهات، قائمة الطلبات
    titleMedium = TextStyle(
        fontFamily = NotoSansArabic,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),
    // النصوص العادية - الرسائل، التفاصيل
    bodyLarge = TextStyle(
        fontFamily = NotoSansArabic,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 18.sp,
    ),
    // النصوص الصغيرة - الوقت، الحالة
    labelSmall = TextStyle(
        fontFamily = NotoSansArabic,
        fontWeight = FontWeight.Light,
        fontSize = 12.sp,
        lineHeight = 16.sp,
    )
)