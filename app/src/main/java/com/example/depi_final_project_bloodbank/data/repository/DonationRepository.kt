package com.example.depi_final_project_bloodbank.data.repository

import com.example.depi_final_project_bloodbank.domain.model.Donation
import com.example.depi_final_project_bloodbank.domain.enums.DonationStatus
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.tasks.await

class DonationRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val donationsCollection = firestore.collection("donations")
    private val requestsCollection = firestore.collection("requests")
    private val notificationsCollection = firestore.collection("notifications")

    suspend fun createDonation(donation: Donation): Boolean {
        return try {
            // 1. نجيب مستند الـ request الحالي
            val requestRef = requestsCollection.document(donation.requestId)
            val requestSnapshot = requestRef.get().await()

            if (!requestSnapshot.exists()) return false

            val unitsNeeded = requestSnapshot.getLong("unitsNeeded") ?: 0L
            val unitsReserved = requestSnapshot.getLong("unitsReserved") ?: 0L

            // 🎯 التعديل هنا: بنقرأ حقل createdBy المطابق للفايربيز عندك
            val userIdOfRequest = requestSnapshot.getString("createdBy") ?: ""

            // 🛑 ليميت الأمان
            if (unitsReserved >= unitsNeeded) {
                return false
            }

            // 2. كارييت مستند التبرع
            val donationRef = donationsCollection.document()
            val finalDonation = donation.copy(
                id = donationRef.id,
                createdAt = System.currentTimeMillis()
            )
            donationRef.set(finalDonation).await()

            // 3. تحديث الوحدات المحجوزة
            requestRef.update("unitsReserved", FieldValue.increment(1)).await()

            // 4. بناء النوتفيكيشن بـ receiverId مضمون
            val notificationId = notificationsCollection.document().id
            val notificationData = mapOf(
                "id" to notificationId,
                // حماية: لو الـ createdBy جاي من الريكويست فاضي، بنحط كلمة "UNKNOWN_USER" كـ داتا احتياطية عشان الكود ميكرشش
                "receiverId" to if (userIdOfRequest.isEmpty()) "UNKNOWN_USER" else userIdOfRequest,
                "title" to "تبرع جديد! 🩸",
                "body" to "قام مستخدم بالموافقة على طلب التبرع الخاص بك، وهو في انتظار تأكيدك.",
                "createdAt" to System.currentTimeMillis(),
                "type" to "DONATION_RECEIVED",
                "requestId" to donation.requestId
            )
            notificationsCollection.document(notificationId).set(notificationData).await()

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    suspend fun cancelDonation(donationId: String): Boolean {
        return try {
            donationsCollection.document(donationId).delete().await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun confirmDonation(donationId: String): Boolean {
        return try {
            donationsCollection.document(donationId)
                .update(
                    "status", DonationStatus.CONFIRMED.name,
                    "confirmedAt", System.currentTimeMillis()
                ).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getDonationsByUser(userId: String): List<Donation> {
        return try {
            donationsCollection.whereEqualTo("donorId", userId)
                .get()
                .await()
                .toObjects(Donation::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getDonationsByRequest(requestId: String): List<Donation> {
        return try {
            donationsCollection.whereEqualTo("requestId", requestId)
                .get()
                .await()
                .toObjects(Donation::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
}