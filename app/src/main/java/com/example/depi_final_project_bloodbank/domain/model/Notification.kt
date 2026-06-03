package com.example.depi_final_project_bloodbank.domain.model
enum class NotificationType {URGENT_REQUEST , DONATION_SUCCESS , REMINDER}
enum class NotificationStatus{ONGOING , COMPLETED , NONE}
data class Notification(
    val id: String,
    val type: NotificationType,
    val title: String,
    val message: String,
    val timeAgo: String,
    val status: NotificationStatus = NotificationStatus.NONE,
    val isRead: Boolean = false
)