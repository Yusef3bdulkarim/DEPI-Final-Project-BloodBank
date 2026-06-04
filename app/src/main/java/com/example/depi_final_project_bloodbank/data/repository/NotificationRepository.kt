package com.example.depi_final_project_bloodbank.data.repository

import com.example.depi_final_project_bloodbank.domain.model.Notification

interface NotificationRepository {
    suspend fun getNotification(): List<Notification>

    suspend fun markAsRead(notificationId: String)

    suspend fun markAllAsRead()
}
