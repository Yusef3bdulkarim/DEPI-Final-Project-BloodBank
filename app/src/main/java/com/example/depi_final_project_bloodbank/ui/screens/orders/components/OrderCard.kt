package com.example.depi_final_project_bloodbank.ui.screens.orders.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.depi_final_project_bloodbank.R
import com.example.depi_final_project_bloodbank.domain.enums.RequestPriority
import com.example.depi_final_project_bloodbank.domain.model.BloodRequest
import com.example.depi_final_project_bloodbank.domain.enums.RequestStatus
import com.example.depi_final_project_bloodbank.ui.screens.orders.RequestUiModel
import com.example.depi_final_project_bloodbank.ui.theme.MaroonPrimary
import com.example.depi_final_project_bloodbank.utils.toFormattedDate

@Composable
fun OrderCard(
    uiModel: RequestUiModel,
    onViewDetailsClicked: (BloodRequest) -> Unit,
    onDonateClicked: (BloodRequest) -> Unit
) {
    val order = uiModel.request
    val titleColor = MaterialTheme.colorScheme.primary
    val secondaryTextColor = MaterialTheme.colorScheme.secondary
    val iconColor = MaterialTheme.colorScheme.onSurface
    
    val statusColor = when (order.status) {
        RequestStatus.COMPLETED -> MaterialTheme.colorScheme.tertiary
        RequestStatus.CANCELLED -> MaterialTheme.colorScheme.error
        RequestStatus.EXPIRED -> MaterialTheme.colorScheme.onSurface
        else -> if (order.priority == RequestPriority.URGENT) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
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
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = order.bloodType,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = if (order.priority == RequestPriority.URGENT) stringResource(R.string.urgent_title, order.bloodType)
                    else stringResource(R.string.request_title, order.bloodType),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (order.priority == RequestPriority.URGENT) MaterialTheme.colorScheme.error else titleColor,
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
                    tint = iconColor
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = order.hospitalName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = secondaryTextColor
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
                    tint = secondaryTextColor
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = order.createdAt.toFormattedDate(),
                    style = MaterialTheme.typography.labelSmall,
                    color = secondaryTextColor
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
                    text = stringResource(R.string.units_fraction, order.unitsReserved, order.unitsNeeded),
                    style = MaterialTheme.typography.labelSmall,
                    color = secondaryTextColor
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
                    color = if (order.status == RequestStatus.COMPLETED) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = if (order.status == RequestStatus.COMPLETED) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
                )
            }

            if (order.status == RequestStatus.EXPIRED) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = stringResource(R.string.expired_request_label),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
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
                    if (uiModel.isOwner) {
                        OutlinedButton(
                            onClick = { onDonateClicked(order) },
                            enabled = uiModel.isButtonEnabled,
                            border = BorderStroke(1.dp, MaroonPrimary),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaroonPrimary
                            ),
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.height(32.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                        ) {
                            Text(
                                text = stringResource(uiModel.buttonTextRes),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    } else if (order.status == RequestStatus.ACTIVE) {
                        Button(
                            onClick = { onDonateClicked(order) },
                            enabled = uiModel.isButtonEnabled,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaroonPrimary,
                                contentColor = Color.White,
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
                                    text = stringResource(uiModel.buttonTextRes),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    } else {
                        Text(
                            text = when (order.status) {
                                RequestStatus.COMPLETED -> stringResource(R.string.delivered)
                                RequestStatus.CANCELLED -> stringResource(R.string.status_cancelled)
                                RequestStatus.EXPIRED -> stringResource(R.string.status_expired)
                                else -> stringResource(R.string.processing)
                            },
                            color = statusColor,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}
