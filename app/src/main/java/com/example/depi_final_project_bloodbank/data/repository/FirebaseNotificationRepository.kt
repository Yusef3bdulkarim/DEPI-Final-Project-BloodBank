package com.example.depi_final_project_bloodbank.data.repository

import android.util.Log
import com.example.depi_final_project_bloodbank.domain.enums.NotificationType
import com.example.depi_final_project_bloodbank.domain.model.Notification
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class FirebaseNotificationRepository : NotificationRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override suspend fun getNotifications(): List<Notification>  {
        val uid = auth.currentUser?.uid ?: return emptyList()
        Log.d("NotifRepo", "Current UID: $uid")
        if (uid == null) {
            Log.d("NotifRepo", "UID is null!")
            return emptyList()
        }
        return try {
            firestore.collection("notification")
                .whereEqualTo("userId", uid)
                .get()
                .await()
                .documents
                .mapNotNull { doc ->
                    try {
                        Notification(
                            id = doc.id,
                            userId = doc.getString("userId") ?: "",
                            type = NotificationType.valueOf(doc.getString("type") ?: "REMINDER"),
                            title = doc.getString("title") ?: "",
                            message = doc.getString("message") ?: "",
                            isRead = doc.getBoolean("isRead") ?: false,
                            relatedId = doc.getString("relatedId") ?: "",
                            createdAt = doc.getTimestamp("createdAt")?.toDate()?.time ?: 0L
                        )
                    } catch (e: Exception) {
                        Log.e("NotifRepo", "Error parsing doc: ${e.message}")
                        null
                    }
                }
        } catch (e: Exception) {
            Log.e("NotifRepo", "Error: ${e.message}")
            emptyList()
        }
    }

    override suspend fun markAsRead(notificationId: String){
        firestore.collection("notification")
            .document(notificationId)
            .update("isRead", true)
            .await()
    }

    override suspend fun markAllAsRead() {
        val uid = auth.currentUser?.uid ?: return
        val batch = firestore.batch()

        firestore.collection("notification")
            .whereEqualTo("userId", uid)
            .whereEqualTo("isRead", false)
            .get()
            .await()
            .documents
            .forEach { doc ->
                batch.update(doc.reference, "isRead", true)
            }

        batch.commit().await()
    }

}