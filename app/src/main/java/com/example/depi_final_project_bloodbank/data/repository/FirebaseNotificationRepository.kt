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
                            // تعديل 1: قراءة الحقل باسم read أو isRead لضمان التوافق
                            isRead = doc.getBoolean("read") ?: doc.getBoolean("isRead") ?: false,
                            relatedId = doc.getString("relatedId") ?: "",
                            fcmToken = doc.getString("fcmToken") ?: "",
                            // تعديل 2: قراءة الرقم كـ Long مباشرة لأنك بتسيفه System.currentTimeMillis()
                            createdAt = doc.getLong("createdAt") ?: 0L
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


    override suspend fun markAsRead(notificationId: String) {
        firestore.collection("notification")
            .document(notificationId)
            .update(mapOf(
                "isRead" to true,
                "read" to true
            ))
            .await()
    }
    override suspend fun markAllAsRead() {
        val uid = auth.currentUser?.uid ?: return
        val batch = firestore.batch()
        val docs = firestore.collection("notification")
            .whereEqualTo("userId", uid)
            .get()
            .await()
            .documents
        docs.forEach { doc ->
            batch.update(doc.reference, mapOf(
                "isRead" to true,
                "read" to true
            ))
        }
        batch.commit().await()
    }

}