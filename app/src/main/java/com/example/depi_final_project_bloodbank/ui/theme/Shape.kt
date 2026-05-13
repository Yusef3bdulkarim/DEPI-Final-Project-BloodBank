package com.example.depi_final_project_bloodbank.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    // للعناصر الصغيرة اللي محتاجة حواف مدورة بس مش كاملة
    // زي الأزرار الصغيرة، حواف الأيقونات في الكروت
    small = RoundedCornerShape(8.dp),

    // للحاويات المتوسطة (وهي الأهم في تصميمك)
    // الكروت (زي كارت التبرع العاجل، كارت المستخدم)
    medium = RoundedCornerShape(12.dp),

    // للحاويات الكبيرة أو العناصر اللي محتاجة استدارة كاملة
    // الـ Chips (زي "جاري"، "مكتمل"، "A+")، وممكن حواف الكروت الكبيرة جداً
    // ملاحظة: الـ 50.dp بتخلي العنصر الصغير زي الـ Chip مستدير تماماً
    large = RoundedCornerShape(50.dp)
)