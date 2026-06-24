package com.example.depi_final_project_bloodbank.ui.screens.notification

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.depi_final_project_bloodbank.domain.enums.NotificationType
import com.example.depi_final_project_bloodbank.ui.screens.notification.components.NotificationCard
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.depi_final_project_bloodbank.R


@Composable
fun NotificationScreen(modifier: Modifier = Modifier ,navController: NavController) {
    val vm: NotificationViewModel = viewModel()
    val state by vm.uiState.collectAsState()
    val count = state.unreadCount
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        NotificationsTopBar(onBackClick = {
            navController.popBackStack()
        })
        if(state.isLoading){
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ){
                CircularProgressIndicator()
            }
        }
        else if(state.notifications.isEmpty()){
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ){
                Text(stringResource(R.string.no_notifications_yet))
            }
        }
        else {
            NotificationsHeader(count = count, onMarkAllRead = { vm.markAllRead() })
            LazyColumn(
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(state.notifications) { notification ->
                    val cardColor =
                        if (notification.isRead) MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
                            .compositeOver(MaterialTheme.colorScheme.background)
                        else MaterialTheme.colorScheme.surface
                    NotificationCard(notification, cardColor = cardColor, onClick = {
                        //لما يدوس علي الاشعار ايه الي حصل
                        //اول م يدوس بتبقي read و بعد كدا بنشوف هيحصل ايه
                        vm.markAsRead(notification.id)
                        when (notification.type) {
                            NotificationType.URGENT_REQUEST -> {
                                // افتح تفاصيل الطلب
                                navController.navigate("requests"){
                                    launchSingleTop = true
                                }
                            }

                            NotificationType.DONATION_SUCCESS -> {
                                // افتح تفاصيل التبرع
                                navController.navigate("requests"){
                                    launchSingleTop = true
                                }
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
            text = stringResource(R.string.notifications_title),
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
                text = stringResource(R.string.new_notifications_count, count),
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Text(
            text = stringResource(R.string.mark_all_as_read),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable { onMarkAllRead() }
        )
    }
}