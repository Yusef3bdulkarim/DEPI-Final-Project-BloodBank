package com.example.depi_final_project_bloodbank.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
val LightColorScheme = lightColorScheme(
    // اللون الأساسي للتطبيق: يُستخدم في الأزرار الرئيسية، الهيدر، والأيقونات النشطة (Active)
    primary = MaroonPrimary,
//    secondary = TextBlack,
    // لون النص أو الأيقونات اللي بتتحط "فوق" الـ primary (زي كلمة "تبرع الآن" جوا الزرار)
    onPrimary = MaroonOnPrimary,

    // لون خلفية خفيفة للعناصر التفاعلية (زي خلفية كارت التبرع، أو الـ Highlights)
    primaryContainer = MaroonContainer,

    // اللون الثانوي: يُستخدم في النصوص العميقة (Deep Navy) والعناصر اللي محتاجة تباين عالي
    secondary = DarkNavy,

    // لون خلفية الشاشة بالكامل (Scaffold background) - درجة رمادي فاتحة جداً تريح العين
    background = BackgroundLight,

    // لون خلفية الكروت (Cards) والـ Sheets والـ Dialogs - دايماً أبيض صافي في تصميمك
    surface = Color.White,

    // لون النص الأساسي اللي بيتكتب فوق الـ surface الأبيض (زي أسماء المستشفيات والتفاصيل)
    onSurface = TextGray,

    // لون التنبيهات والأخطاء والحالات العاجلة (Urgent) - الأحمر الصريح
    error = UrgentRed ,

    // اللون الثالث (Success): يُستخدم لحالات النجاح، الإنجاز، والعناصر الإيجابية (اللون الأخضر)
    tertiary = SuccessGreen,

    // لون النص أو الأيقونة فوق الأخضر (عشان يبان بوضوح)
    onTertiary = Color.White,
)
@Composable
fun DEPIFinalProjectBloodBankTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content,
        shapes = Shapes
    )
}