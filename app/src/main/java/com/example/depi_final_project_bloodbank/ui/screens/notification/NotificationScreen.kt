package com.example.depi_final_project_bloodbank.ui.screens.notification

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.depi_final_project_bloodbank.domain.enums.NotificationType
import com.example.depi_final_project_bloodbank.domain.model.Notification
import com.example.depi_final_project_bloodbank.ui.screens.notification.components.NotificationCard

val notifications = listOf(

    Notification(
        id = "notif_1",
        userId = "user_1",
        type = NotificationType.URGENT_REQUEST,
        title = "Urgent Blood Request",
        message = "Al Salam Hospital urgently needs O+ blood donors in Sohag.",
        isRead = false,
        relatedId = "request_101",
        createdAt = System.currentTimeMillis()
    ),

    Notification(
        id = "notif_2",
        userId = "user_1",
        type = NotificationType.DONATION_SUCCESS,
        title = "Donation Confirmed",
        message = "Thank you for completing your blood donation.",
        isRead = true,
        relatedId = "donation_201",
        createdAt = System.currentTimeMillis() - 2 * 60 * 60 * 1000
    ),

    Notification(
        id = "notif_3",
        userId = "user_1",
        type = NotificationType.REMINDER,
        title = "Donation Reminder",
        message = "You are now eligible to donate blood again.",
        isRead = false,
        relatedId = "",
        createdAt = System.currentTimeMillis() - 24 * 60 * 60 * 1000
    ),

    Notification(
        id = "notif_4",
        userId = "user_1",
        type = NotificationType.URGENT_REQUEST,
        title = "New Emergency Request",
        message = "A+ blood is needed at Sohag University Hospital.",
        isRead = false,
        relatedId = "request_102",
        createdAt = System.currentTimeMillis() - 30 * 60 * 1000
    ),

    Notification(
        id = "notif_5",
        userId = "user_1",
        type = NotificationType.DONATION_SUCCESS,
        title = "Request Completed",
        message = "The blood request you contributed to has been fulfilled.",
        isRead = true,
        relatedId = "request_103",
        createdAt = System.currentTimeMillis() - 3 * 60 * 60 * 1000
    )
)

@Composable
fun NotificationScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        NotificationsTopBar(onBackClick = {})
        NotificationsHeader(count = notifications.count { !it.isRead }, onMarkAllRead = {})
        LazyColumn() {
            items(notifications) { notification ->
                NotificationCard(notification, onClick = {
                    //لما يدوس علي الاشعار ايه الي حصل
                    when (notification.type) {
                        NotificationType.URGENT_REQUEST -> {
                            // افتح تفاصيل الطلب
                        }

                        NotificationType.DONATION_SUCCESS -> {
                            // افتح تفاصيل التبرع
                        }

                        NotificationType.REMINDER -> {
                            // افتح صفحة التبرعات أو البروفايل
                        }
                    }
                })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsTopBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "back",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Text(
            text = "Notifications",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
fun NotificationsHeader(count: Int, onMarkAllRead: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "You have $count new notifications",
                style = MaterialTheme.typography.labelSmall,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Text(
            text = "Mark all as read",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable { onMarkAllRead() }
        )
    }
}