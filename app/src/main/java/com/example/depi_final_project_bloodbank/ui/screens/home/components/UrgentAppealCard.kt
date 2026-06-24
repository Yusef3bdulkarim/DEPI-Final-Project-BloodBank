package com.example.depi_final_project_bloodbank.ui.screens.home.components

import android.R.id.primary
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.depi_final_project_bloodbank.R
private val ChipWhite = Color(0xFFFFFFFF)

@Composable
fun UrgentAppealCard(
    hospitalName: String,
    location: String,
    units: Int,
    bloodType: String,
    isUrgent: Boolean = true,
    onClickView: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.small
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween // بيخلي الـ Row في الأول والـ Column في الآخر تماماً
            ) {
                // 1. الـ Row اللي في البداية (الأيقونة والنصوص)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp), // مسافة ثابتة وذكية بين العناصر بدل الـ Spacers الكتير
                    modifier = Modifier.weight(1f) // بياخد المساحة المتاحة وميزقش الـ Column اللي بره
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.warning_blood),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(35.dp)
                    )

                    if (isUrgent) {
                        Text(
                            text = stringResource(R.string.urgent_label),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    Text(
                        text = bloodType,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isUrgent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                    )
                }

                // 2. الـ Column اللي في النهاية (اسم المستشفى والموقع)
                Column(
                    horizontalAlignment = Alignment.End // بيحاذي النصوص لآخر الشاشة جهة اليمين/اليسار حسب الـ RTL
                ) {
                    Text(
                        text = hospitalName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary,
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = location,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray,
                            maxLines = 1
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(5.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // معلومات المستشفى والمسافة
                Column {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            stringResource(R.string.needs_units, units),
                            style = MaterialTheme.typography.labelSmall,
                            color =MaterialTheme.colorScheme.onSurface,
                        )


                        Surface(
                            shape           = RoundedCornerShape(12.dp),
                            color           =MaterialTheme.colorScheme.primary,
                            shadowElevation = 4.dp
                            , modifier = Modifier.size(height = 30.dp, width = 55.dp),
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                TextButton(onClick = onClickView, modifier = Modifier.padding(0.dp)) {
                                    Text(stringResource(R.string.view_label),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = ChipWhite)
                                }
                            }
                        }


                    }
                    Spacer(modifier = Modifier.height(5.dp))

                }

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun UrgentAppealCardPrev() {
    UrgentAppealCard("Kafr-Elshiekh Blood-Bank", "Kafr El-Sheikh", 1, "A+") {}
}