package com.example.depi_final_project_bloodbank.ui.screens.home.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.example.depi_final_project_bloodbank.R
import com.example.depi_final_project_bloodbank.data.local.HealthTipPreferences

@Composable
fun DynamicHealthTipsSection() {
    val tips = listOf(
        HealthTip(titleRes = R.string.tip_title_1, descriptionRes = R.string.tip_desc_1, iconRes = R.drawable.heart_fact),
        HealthTip(titleRes = R.string.tip_title_2, descriptionRes = R.string.tip_desc_2, iconRes = R.drawable.heart_fact),
        HealthTip(titleRes = R.string.tip_title_3, descriptionRes = R.string.tip_desc_3, iconRes = R.drawable.heart_fact),
        HealthTip(titleRes = R.string.tip_title_4, descriptionRes = R.string.tip_desc_4, iconRes = R.drawable.heart_fact),
        HealthTip(titleRes = R.string.tip_title_5, descriptionRes = R.string.tip_desc_5, iconRes = R.drawable.heart_fact),
        HealthTip(titleRes = R.string.tip_title_6, descriptionRes = R.string.tip_desc_6, iconRes = R.drawable.heart_fact),
    )

    val context = LocalContext.current
    val tipIndex = remember { HealthTipPreferences.getTipIndex(context, tips.size) }
    val tip = tips[tipIndex]

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .padding(top = 8.dp, bottom = 24.dp, start = 16.dp, end = 16.dp),
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
                        text = stringResource(R.string.fact_prefix, stringResource(tip.titleRes)),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = stringResource(tip.descriptionRes),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = TextUnit(16f, TextUnitType.Sp)
                    )
                }
            }
        }
    }
}

// تعديل الكلاس ليدعم كلاً من الـ Drawable ID والـ ImageVector بمرونة كاملة
data class HealthTip(
    val titleRes: Int,
    val descriptionRes: Int,
    val iconRes: Int? = null,
    val iconVector: androidx.compose.ui.graphics.vector.ImageVector? = null
)

@Preview(showBackground = true)
@Composable
private fun DynamicHealthTipsSectionPrev() {
    DynamicHealthTipsSection()
}