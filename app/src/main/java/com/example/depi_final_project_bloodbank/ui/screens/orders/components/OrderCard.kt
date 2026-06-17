package com.example.depi_final_project_bloodbank.ui.screens.orders.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.depi_final_project_bloodbank.R
import com.example.depi_final_project_bloodbank.domain.enums.RequestPriority
import com.example.depi_final_project_bloodbank.domain.model.BloodRequest
import com.example.depi_final_project_bloodbank.domain.enums.RequestStatus
import com.example.depi_final_project_bloodbank.ui.screens.orders.RequestUiModel
import com.example.depi_final_project_bloodbank.utils.toFormattedDate

@Composable
fun OrderCard(
    uiModel: RequestUiModel,
    onViewDetailsClicked: (BloodRequest) -> Unit,
    onDonateClicked: (BloodRequest) -> Unit
) {
    val order = uiModel.request
    val appUnifiedBlack = MaterialTheme.colorScheme.secondary
    val hospitalIconGray = MaterialTheme.colorScheme.onSurface
    val statusColor = when {
        order.priority == RequestPriority.URGENT && order.status == RequestStatus.ACTIVE -> MaterialTheme.colorScheme.error
        order.status == RequestStatus.COMPLETED -> MaterialTheme.colorScheme.tertiary
        order.status == RequestStatus.CANCELLED -> MaterialTheme.colorScheme.error
        order.status == RequestStatus.EXPIRED -> MaterialTheme.colorScheme.onSurface
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

            // Header Section
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            if (order.priority == RequestPriority.URGENT) statusColor.copy(alpha = 0.1f)
                            else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = order.bloodType,
                        color = if (order.priority == RequestPriority.URGENT) statusColor else MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = if (order.priority == RequestPriority.URGENT) stringResource(R.string.urgent_title, order.bloodType)
                    else stringResource(R.string.request_title, order.bloodType),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (order.priority == RequestPriority.URGENT) statusColor else appUnifiedBlack,
                    modifier = Modifier.weight(1f),
                    softWrap = true
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Metadata Section
            Row(
                modifier = Modifier.padding(start = 60.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocalHospital,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = hospitalIconGray
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = order.hospitalName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = appUnifiedBlack
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
                    tint = appUnifiedBlack
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = order.createdAt.toFormattedDate(),
                    style = MaterialTheme.typography.labelSmall,
                    color = appUnifiedBlack
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
                    text = "${order.unitsReserved}/${order.unitsNeeded} units",
                    style = MaterialTheme.typography.labelSmall,
                    color = appUnifiedBlack
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Indicator Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                val progress = when {
                    order.status == RequestStatus.COMPLETED -> 1.0f
                    order.unitsNeeded > 0 -> order.unitsReserved.toFloat() / order.unitsNeeded.toFloat()
                    else -> 0f
                }
                val animatedProgress by animateFloatAsState(
                    targetValue = progress,
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
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = statusColor
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Row
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

                Box(
                    modifier = Modifier.wrapContentWidth(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Button(
                        onClick = { onDonateClicked(order) },
                        enabled = uiModel.isButtonEnabled,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (uiModel.isOwner) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                            contentColor = if (uiModel.isOwner) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onPrimary,
                            disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                        ),
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.height(32.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                    ) {
                        if (uiModel.isDonating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = uiModel.buttonText,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }
        }
    }
}
