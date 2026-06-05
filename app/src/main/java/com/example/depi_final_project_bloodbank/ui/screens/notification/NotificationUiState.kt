package com.example.depi_final_project_bloodbank.ui.screens.notification

import com.example.depi_final_project_bloodbank.domain.model.Notification

data class NotificationUiState(
    val notifications : List<Notification> = emptyList(),
    val isLoading : Boolean = false,
    //val unreadCount : Int = 0
){
    val unreadCount: Int get() = notifications.count { !it.isRead }
}
