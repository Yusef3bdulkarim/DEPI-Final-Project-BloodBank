package com.example.depi_final_project_bloodbank.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.example.depi_final_project_bloodbank.R

@Composable
fun DynamicHealthTipsSection() {
    val tips = listOf(
        // 1. النصيحة الأولى الأصلية
        HealthTip(
            title = "SAVES 3 LIVES",
            description = "تبرع بـ 450 مل ينقذ حياة 3 أشخاص\n(Donate 450ml to save 3 lives)",
            iconRes = R.drawable.heart_fact,
        ),
        // 2. النصيحة الثانية الأصلية (باستخدام الـ Drawable الخاص بك)
        HealthTip(
            iconRes = R.drawable.heart_fact,
            title = "STAY HYDRATED",
            description = "اشرب الكثير من الماء قبل وبعد التبرع بالدم لضمان سلامتك."
        ),
        // 3. النصيحة الثالثة الجديدة: الوجبات الصحية
        HealthTip(
            title = "HEALTHY MEAL",
            description = "تناول وجبة خفيفة غنية بالحديد وتجنب الأطعمة الدهنية قبل التبرع.",
            iconRes = R.drawable.heart_fact,
        ),
        // 4. النصيحة الرابعة الجديدة: فترة الراحة بعد التبرع
        HealthTip(
            title = "REST & RECOVER",
            description = "استرخِ لمدة 10-15 دقيقة بعد التبرع وتجنب المجهود البدني الشاق.",
            iconRes = R.drawable.heart_fact,
        ),
        // 5. النصيحة الخامسة الجديدة: شروط نسبة الهيموجلوبين
        HealthTip(
            title = "HEMOGLOBIN CHECK",
            description = "تأكد من أن نسبة الهيموجلوبين لديك مناسبة قبل البدء في عملية التبرع.",
            iconRes = R.drawable.heart_fact,
        ),
        // 6. النصيحة السادسة الجديدة: تكرار التبرع بالدم
        HealthTip(
            title = "DONATION INTERVAL",
            description = "يمكن للرجال التبرع كل 3 أشهر، وللنساء كل 4 أشهر بأمان تام.",
            iconRes = R.drawable.heart_fact,
        )
    )

    val pagerState = rememberPagerState(pageCount = { tips.size })

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) { page ->
            val tip = tips[page]
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp).padding(horizontal = 5.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = MaterialTheme.shapes.medium,
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(Color.Transparent, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        // تحديد الـ Painter بناءً على ما إذا كان هناك ملف Drawable أو أيقونة Vector
                        val iconPainter: Painter = when {
                            tip.iconRes != null -> painterResource(id = tip.iconRes)
                            tip.iconVector != null -> androidx.compose.ui.graphics.vector.rememberVectorPainter(tip.iconVector)
                            else -> androidx.compose.ui.graphics.vector.rememberVectorPainter(Icons.Default.Favorite) // أيقونة احتياطية
                        }

                        Icon(
                            painter = iconPainter,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(50.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "FACT: ${tip.title}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
//                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = tip.description,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = TextUnit(16f, TextUnitType.Sp)
                        )
                    }
                }
            }
        }

        // مؤشر الصفحات السفلي (Dots Indicator) سيتكيف تلقائياً مع الـ 6 عناصر
        Row(
            Modifier
                .height(20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(tips.size) { iteration ->
                val color =
                    if (pagerState.currentPage == iteration) MaterialTheme.colorScheme.primary else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(6.dp)
                )
            }
        }
    }
}

// تعديل الكلاس ليدعم كلاً من الـ Drawable ID والـ ImageVector بمرونة كاملة
data class HealthTip(
    val title: String,
    val description: String,
    val iconRes: Int? = null,
    val iconVector: androidx.compose.ui.graphics.vector.ImageVector? = null
)

@Preview(showBackground = true)
@Composable
private fun DynamicHealthTipsSectionPrev() {
    DynamicHealthTipsSection()
}