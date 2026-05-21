package com.example.depi_final_project_bloodbank.ui.screens.notification.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.depi_final_project_bloodbank.domain.model.NotificationItem
import com.example.depi_final_project_bloodbank.domain.model.NotificationStatus
import com.example.depi_final_project_bloodbank.domain.model.NotificationType

@Composable
fun NotificationCard(notification: NotificationItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(
                onClick = onClick,
            ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.small
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.Top
            ) {

                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NotificationIcon(type = notification.type)
                    Column(
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp)
                    ) {
                        Text(
                            text = notification.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = notification.message,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        if (notification.status != NotificationStatus.NONE) {
                            Text(
                                text = if (notification.status == NotificationStatus.ONGOING)
                                    "Status: Ongoing" else "Status: Completed",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (notification.status == NotificationStatus.ONGOING)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }

                }
                Text(
                    text = notification.timeAgo,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }

            if (notification.status != NotificationStatus.NONE) {
                val progress = if (notification.status == NotificationStatus.COMPLETED) 1f
                else 0.5f
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp),
                    color = if (notification.status == NotificationStatus.COMPLETED)
                        MaterialTheme.colorScheme.tertiary
                    else
                        MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                )
            }
        }
    }
}

@Composable
fun NotificationIcon(type: NotificationType) {
    val (icon, bgColor, iconColor) = when (type) {
        NotificationType.URGENT_REQUEST -> Triple(
            Icons.Default.Favorite,
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.primary
        )

        NotificationType.DONATION_SUCCESS -> Triple(
            Icons.Default.CheckCircle,
            Color(0xFFC7F5C8),
            MaterialTheme.colorScheme.tertiary
        )

        NotificationType.REMINDER -> Triple(
            Icons.Default.DateRange,
            Color(0xFFFCE2BD),
            Color(0xFFFF9800),
        )
    }

    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )
    }
}