package com.example.depi_final_project_bloodbank.models
enum class NotificationType {URGENT_REQUEST , DONATION_SUCCESS , REMINDER}
enum class NotificationStatus{ONGOING , COMPLETED , NONE}
data class NotificationItem(
    val id: String,
    val type: NotificationType,
    val title: String,
    val message: String,
    val timeAgo: String,
    val status: NotificationStatus = NotificationStatus.NONE,
    val isRead: Boolean = false
)