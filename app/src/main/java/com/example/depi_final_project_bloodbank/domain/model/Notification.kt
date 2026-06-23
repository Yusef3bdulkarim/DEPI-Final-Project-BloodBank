package com.example.depi_final_project_bloodbank.domain.model

import com.example.depi_final_project_bloodbank.domain.enums.NotificationType

data class Notification(
    val id: String = "",
    val userId: String = "",
    val type: NotificationType = NotificationType.REMINDER,
    val title: String = "",
    val message: String = "",
    val isRead: Boolean = false,
    val relatedId: String = "",
    val fcmToken: String = "",
    val createdAt: Long = 0L
)