package com.example.depi_final_project_bloodbank.ui.screens.notification

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.depi_final_project_bloodbank.domain.model.NotificationItem
import com.example.depi_final_project_bloodbank.domain.model.NotificationStatus
import com.example.depi_final_project_bloodbank.domain.model.NotificationType
import com.example.depi_final_project_bloodbank.ui.screens.notification.components.NotificationCard

val notifications = listOf(
    NotificationItem(
        id = "1",
        type = NotificationType.URGENT_REQUEST,
        title = "Urgent Blood Donation Request",
        message = "Al Salam Hospital needs O+ blood type near your location",
        timeAgo = "Now",
        status = NotificationStatus.ONGOING
    ),
    NotificationItem(
        id = "2",
        type = NotificationType.DONATION_SUCCESS,
        title = "Donation Successful",
        message = "Thank you for donating at Military Hospital",
        timeAgo = "2 hours ago",
        status = NotificationStatus.COMPLETED
    ),
    NotificationItem(
        id = "3",
        type = NotificationType.REMINDER,
        title = "Appointment Reminder",
        message = "You have a donation appointment tomorrow at 10 AM",
        timeAgo = "5 hours ago",
        status = NotificationStatus.NONE
    ),
    NotificationItem(
        id = "1",
        type = NotificationType.URGENT_REQUEST,
        title = "Urgent Blood Donation Request",
        message = "Al Salam Hospital needs O+ blood type near your location",
        timeAgo = "Now",
        status = NotificationStatus.ONGOING
    ),
    NotificationItem(
        id = "2",
        type = NotificationType.DONATION_SUCCESS,
        title = "Donation Successful",
        message = "Thank you for donating at Military Hospital",
        timeAgo = "2 hours ago",
        status = NotificationStatus.COMPLETED
    ),
    NotificationItem(
        id = "3",
        type = NotificationType.REMINDER,
        title = "Appointment Reminder",
        message = "You have a donation appointment tomorrow at 10 AM",
        timeAgo = "5 hours ago",
        status = NotificationStatus.NONE
    )
)
@Composable
fun NotificationScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        NotificationsTopBar(onBackClick = {})
        NotificationsHeader(count = 5 , onMarkAllRead = {})
        LazyColumn() {
            items(notifications){notification ->
                NotificationCard(notification , onClick = {})
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