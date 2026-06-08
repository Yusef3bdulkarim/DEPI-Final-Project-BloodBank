package com.example.depi_final_project_bloodbank.ui.screens.profile.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {

    Card(
        modifier = modifier.height(80.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 4.dp), // ضفنا مسافة أمان يمين وشمال عشان الكلام ميبقاش لازق
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                // هنا التريك: لو الكلمة أكتر من 4 حروف، صغر الخط لـ 14sp، غير كده سيبه بحجمه الطبيعي
                fontSize = if (value.length > 4) 14.sp else TextUnit.Unspecified,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center, // عشان الكلمة تبقى في النص دايماً
                maxLines = 1, // اجبره يبقى في سطر واحد
                overflow = TextOverflow.Ellipsis // لو الشاشة صغيرة جداً هيحط ... في الآخر بدل ما يبوظ التصميم
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}