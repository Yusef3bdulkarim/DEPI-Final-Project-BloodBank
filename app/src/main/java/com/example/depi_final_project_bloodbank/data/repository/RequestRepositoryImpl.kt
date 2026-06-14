package com.example.depi_final_project_bloodbank.data.repository

import com.example.depi_final_project_bloodbank.domain.enums.DonationStatus
import com.example.depi_final_project_bloodbank.domain.enums.RequestStatus
import com.example.depi_final_project_bloodbank.domain.model.BloodRequest
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class RequestRepositoryImpl(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : RequestRepository {

    private val requestsCollection = firestore.collection("requests")

    override suspend fun createRequest(request: BloodRequest): Result<Boolean> {
        return try {
            val docRef = requestsCollection.document()
            val finalRequest = request.copy(
                id = docRef.id,
                createdAt = System.currentTimeMillis()
            )
            docRef.set(finalRequest).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getActiveRequests(
        onSuccess: (List<BloodRequest>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        requestsCollection
            .whereEqualTo("status", "ACTIVE")
            .limit(10)
            .get()
            .addOnSuccessListener { snapshot ->
                val requests = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(BloodRequest::class.java)?.copy(id = doc.id)
                }
                onSuccess(requests)
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    override fun getAllRequests(): Flow<List<BloodRequest>> = callbackFlow {
        val query = requestsCollection.orderBy("createdAt", Query.Direction.DESCENDING)

        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val requests = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(BloodRequest::class.java)?.copy(id = doc.id)
                }
                trySend(requests).isSuccess
            }
        }
        awaitClose { listener.remove() }
    }

    override fun getRequestsByCity(city: String): Flow<List<BloodRequest>> = callbackFlow {
        val query = requestsCollection
            .whereEqualTo("city", city)
            .whereEqualTo("status", RequestStatus.ACTIVE.name)

        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val requests = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(BloodRequest::class.java)?.copy(id = doc.id)
                }
                trySend(requests).isSuccess
            }
        }
        awaitClose { listener.remove() }
    }

    override suspend fun getRequestById(id: String): Result<BloodRequest?> {
        return try {
            val doc = requestsCollection.document(id).get().await()
            val request = doc.toObject(BloodRequest::class.java)?.copy(id = doc.id)
            Result.success(request)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateRequestStatus(id: String, status: RequestStatus): Result<Boolean> {
        return try {
            requestsCollection.document(id).update("status", status.name).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun incrementReservedUnits(id: String): Result<Boolean> {
        return try {
            requestsCollection.document(id).update("unitsReserved", FieldValue.increment(1)).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun incrementConfirmedUnits(id: String): Result<Boolean> {
        return try {
            requestsCollection.document(id).update("unitsConfirmed", FieldValue.increment(1)).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 🔥 الـ Transaction الذكي: بيقرأ من السيرفر الأول ويقفل لو الليميت خلص
    // تم تطويره ليكون ذرياً (Atomic) بالكامل ليشمل حجز الوحدة وإنشاء التبرع وإرسال الإشعار
    override suspend fun safeIncrementReservedUnits(id: String, donorId: String): Result<Boolean> {
        return try {
            val docRef = requestsCollection.document(id)
            val donationRef = firestore.collection("donations").document()
            val notificationRef = firestore.collection("notifications").document()

            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(docRef)
                if (!snapshot.exists()) throw Exception("Request not found")

                val currentReserved = snapshot.getLong("unitsReserved") ?: 0L
                val needed = snapshot.getLong("unitsNeeded") ?: 0L
                val userIdOfRequest = snapshot.getString("createdBy") ?: ""

                if (currentReserved >= needed) {
                    throw Exception("Limit reached")
                }

                // 1. تحديث الطلب (زيادة الوحدات المحجوزة)
                transaction.update(docRef, "unitsReserved", currentReserved + 1)

                // 2. إنشاء مستند التبرع
                val donationData = mapOf(
                    "id" to donationRef.id,
                    "donorId" to donorId,
                    "requestId" to id,
                    "status" to DonationStatus.PENDING.name,
                    "createdAt" to System.currentTimeMillis(),
                    "timestamp" to System.currentTimeMillis() // حقل إضافي لتوثيق الوقت كما هو مطلوب
                )
                transaction.set(donationRef, donationData)

                // 3. إنشاء الإشعار لصاحب الطلب
                val notificationData = mapOf(
                    "id" to notificationRef.id,
                    "receiverId" to if (userIdOfRequest.isEmpty()) "UNKNOWN_USER" else userIdOfRequest,
                    "title" to "تبرع جديد! 🩸",
                    "body" to "قام مستخدم بالموافقة على طلب التبرع الخاص بك، وهو في انتظار تأكيدك.",
                    "createdAt" to System.currentTimeMillis(),
                    "type" to "DONATION_RECEIVED",
                    "requestId" to id
                )
                transaction.set(notificationRef, notificationData)
            }.await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}