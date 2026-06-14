package com.example.depi_final_project_bloodbank.ui.screens.notification.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
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
import com.example.depi_final_project_bloodbank.domain.enums.NotificationType
import com.example.depi_final_project_bloodbank.domain.model.Notification
import com.example.depi_final_project_bloodbank.utils.toTimeAgo

@Composable

fun NotificationCard(notification: Notification,cardColor : Color,onClick: () -> Unit) {
    val contentAlpha = if (notification.isRead) 0.5f else 1f
    val lineColor = when (notification.type) {
        NotificationType.URGENT_REQUEST -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = contentAlpha)
        NotificationType.DONATION_SUCCESS ->Color(0xFFC7F5C8).copy(alpha = contentAlpha)
        NotificationType.REMINDER ->Color(0xFFFCE2BD).copy(alpha = contentAlpha)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(
                onClick = onClick,
            ),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = Modifier.height(IntrinsicSize.Min)
                .background(cardColor),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Box(
                modifier = Modifier.fillMaxHeight()
                    .width(8.dp)
                    .background(
                        lineColor,
                    )
            ) { }
            Column() {
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
                        NotificationIcon(type = notification.type , notification.isRead)
                        Column(
                            horizontalAlignment = Alignment.Start,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp)
                        ) {
                            Text(
                                text = notification.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.secondary.copy(alpha = contentAlpha)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = notification.message,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = contentAlpha)
                            )
                        }

                    }
                    Text(
                        text = notification.createdAt.toTimeAgo(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = if(notification.isRead) 0.3f else 0.7f)
                    )
                }

            }
        }
    }
}

@Composable
fun NotificationIcon(type: NotificationType , isRead: Boolean) {
    val contentAlpha = if (isRead) 0.5f else 1f
    val (icon, bgColor, iconColor) = when (type) {
        NotificationType.URGENT_REQUEST -> Triple(
            Icons.Default.Favorite,
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = contentAlpha),
            MaterialTheme.colorScheme.primary.copy(alpha = contentAlpha)
        )

        NotificationType.DONATION_SUCCESS -> Triple(
            Icons.Default.CheckCircle,
            Color(0xFFC7F5C8).copy(alpha = contentAlpha),
            MaterialTheme.colorScheme.tertiary.copy(alpha = contentAlpha)
        )

        NotificationType.REMINDER -> Triple(
            Icons.Default.DateRange,
            Color(0xFFFCE2BD).copy(alpha = contentAlpha),
            Color(0xFFFF9800).copy(alpha = contentAlpha),
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