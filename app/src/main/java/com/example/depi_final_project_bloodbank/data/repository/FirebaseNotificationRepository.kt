package com.example.depi_final_project_bloodbank.data.repository

import com.example.depi_final_project_bloodbank.domain.enums.NotificationType
import com.example.depi_final_project_bloodbank.domain.model.Notification

class FirebaseNotificationRepository : NotificationRepository {
    private val notifications = mutableListOf(

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


    override suspend fun getNotifications(): List<Notification>  {
        return notifications
    }

    override suspend fun markAsRead(notificationId: String){
        val index = notifications.indexOfFirst {
            it.id == notificationId
        }
        if (index != -1) {
            notifications[index] =
                notifications[index].copy(isRead = true)
        }
    }

    override suspend fun markAllAsRead() {
        notifications.replaceAll{
            it.copy(isRead = true)
        }
    }

}