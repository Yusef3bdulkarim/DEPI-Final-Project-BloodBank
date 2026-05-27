package com.example.depi_final_project_bloodbank.ui.screens.orders.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.example.depi_final_project_bloodbank.R
import com.example.depi_final_project_bloodbank.domain.model.BloodReq
import com.example.depi_final_project_bloodbank.domain.model.RequestStatus

@Composable
fun OrderCard(
    order: BloodReq,
    onViewDetailsClicked: (BloodReq) -> Unit,
    onDonateClicked: (BloodReq) -> Unit
) {
    // 🎯 توظيف الألوان الموحدة من الـ Theme بتاعك بالظبط لتجنب أي إيرور
    val appUnifiedBlack = MaterialTheme.colorScheme.secondary      // الـ DarkNavy (الأسود الموحد للبرنامج)
    val hospitalIconGray = MaterialTheme.colorScheme.onSurface     // الـ TextGray (الرمادي الموحد للوجو المستشفى)

    val statusColor = when {
        order.isUrgent -> MaterialTheme.colorScheme.error
        order.status == RequestStatus.COMPLETED -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onViewDetailsClicked(order) },
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // 2. Header Section (Badge & Title)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            if (order.isUrgent) statusColor.copy(alpha = 0.1f)
                            else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = order.bloodType,
                        color = if (order.isUrgent) statusColor else MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = if (order.isUrgent) stringResource(R.string.urgent_title, order.bloodType)
                    else stringResource(R.string.request_title, order.bloodType),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (order.isUrgent) statusColor else appUnifiedBlack, // 👈 المارون لو عاجل، والأسود الموحد لو عادي
                    modifier = Modifier.weight(1f),
                    softWrap = true
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 3. Metadata Sub-Header (Hospital, Time, and Blood Units)
            Row(
                modifier = Modifier.padding(start = 60.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocalHospital,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = hospitalIconGray // 👈 لوجو المستشفى بالرمادي الموحد (TextGray)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(order.hospital),
                    style = MaterialTheme.typography.bodyMedium,
                    color = appUnifiedBlack // 👈 اسم المستشفى بالأسود الموحد (DarkNavy)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.padding(start = 60.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = appUnifiedBlack // 👈 أيقونة الوقت بالأسود الموحد
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(order.date),
                    style = MaterialTheme.typography.labelSmall,
                    color = appUnifiedBlack // 👈 نص الوقت بالأسود الموحد
                )

                Spacer(modifier = Modifier.width(16.dp))

                Icon(
                    imageVector = Icons.Default.Bloodtype,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = statusColor
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(R.string.unit_count, order.units),
                    style = MaterialTheme.typography.labelSmall,
                    color = appUnifiedBlack // 👈 نص وحدات الدم بالأسود الموحد
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Progress Indicator & Percentage Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                val animatedProgress by animateFloatAsState(
                    targetValue = order.progress,
                    animationSpec = tween(durationMillis = 1000),
                    label = "ProgressAnimation"
                )

                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .weight(1f)
                        .height(3.dp)
                        .clip(CircleShape),
                    color = statusColor,
                    trackColor = hospitalIconGray.copy(alpha = 0.2f)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "${(order.progress * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = statusColor
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 5. Action Row (Buttons & Donor Avatars)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                TextButton(
                    onClick = { onViewDetailsClicked(order) },
                    contentPadding = PaddingValues(horizontal = 0.dp)
                ) {
                    Text(
                        text = stringResource(R.string.view_details),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.wrapContentWidth(),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        if (order.status == RequestStatus.URGENT) {
                            Button(
                                onClick = { onDonateClicked(order) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                shape = MaterialTheme.shapes.medium,
                                modifier = Modifier.height(32.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.donate_now),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        } else {
                            Text(
                                text = if (order.status == RequestStatus.COMPLETED)
                                    stringResource(R.string.delivered)
                                else stringResource(R.string.processing),
                                color = statusColor,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Box(modifier = Modifier.width(70.dp), contentAlignment = Alignment.CenterEnd) {
                        DonorsSection(count = order.donorsCount)
                    }
                }
            }
        }
    }
}

@Composable
fun DonorsSection(count: Int) {
    val maxImages = 3
    val displayCount = count.coerceAtMost(maxImages)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy((-12).dp)
    ) {
        repeat(displayCount) { index ->
            AsyncImage(
                model = "https://i.pravatar.cc/150?u=$index",
                contentDescription = null,
                modifier = Modifier
                    .size(28.dp)
                    .zIndex((maxImages - index).toFloat())
                    .clip(CircleShape)
                    .border(1.dp, Color.White, CircleShape),
                contentScale = ContentScale.Crop
            )
        }
        if (count > maxImages) {
            Surface(
                modifier = Modifier
                    .size(28.dp)
                    .zIndex(0f)
                    .clip(CircleShape)
                    .border(1.dp, Color.White, CircleShape),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "+${count - maxImages}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}