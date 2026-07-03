package com.example.depi_final_project_bloodbank.data.repository

import com.example.depi_final_project_bloodbank.domain.model.Donation
import com.example.depi_final_project_bloodbank.domain.enums.DonationStatus
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import com.example.depi_final_project_bloodbank.domain.model.BloodRequest
import com.example.depi_final_project_bloodbank.domain.model.User
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map

class DonationRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val donationsCollection = firestore.collection("donations")
    private val requestsCollection = firestore.collection("requests")

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


    fun observeTotalConfirmedDonations(userId: String): Flow<Int> = callbackFlow {
        val listener = requestsCollection
            .whereArrayContains("donorIds", userId) // نبحث عن كل الطلبات التي شارك فيها المستخدم
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                // نحصل على الطلبات ونقوم بعد التبرعات التي تحمل حالة CONFIRMED وتخص هذا المستخدم
                val requests = snapshot?.toObjects(BloodRequest::class.java) ?: emptyList()
                val totalConfirmed = requests.sumOf { request ->
                    request.donationLog.count { it.donorId == userId && it.status == DonationStatus.CONFIRMED }
                }

                trySend(totalConfirmed)
            }

        awaitClose { listener.remove() }
    }

    // 2. دالة مراقبة لستة التبرعات لايف لشاشة السجل (History)
    fun observeDonationsByUser(userId: String): Flow<List<Donation>> = callbackFlow {
        val listener = donationsCollection
            .whereEqualTo("donorId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val donations = snapshot?.toObjects(Donation::class.java) ?: emptyList()
                trySend(donations)
            }
        awaitClose { listener.remove() }
    }


    fun getFilteredRequests(userCity: String): Flow<List<BloodRequest>> {
        return firestore.collection("blood_requests")
            .whereEqualTo("city", userCity) // 1. فلترة المحافظة
            .whereEqualTo("status", "ACTIVE")
            .snapshots()
            .map { it.toObjects(BloodRequest::class.java) }
    }

    suspend fun executeDonation(donation: Donation, user: User): Result<Unit> {
        val NINETY_DAYS = 90L * 24 * 60 * 60 * 1000

        // 2. شرط الـ 90 يوم
        user.lastDonationDate?.let {

            if ((System.currentTimeMillis() - it) < NINETY_DAYS) {
                return Result.failure(Exception("يجب الانتظار 90 يوماً من آخر تبرع."))
            }
        }

        // 3. شرط الوحدة الواحدة
        if (donation.units > 1) {
            return Result.failure(Exception("مسموح بوحدة واحدة فقط."))
        }

        // تنفيذ عملية الحفظ في Firebase...
        return Result.success(Unit)
    }


}