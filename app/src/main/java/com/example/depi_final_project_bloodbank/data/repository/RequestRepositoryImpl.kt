package com.example.depi_final_project_bloodbank.data.repository

import com.example.depi_final_project_bloodbank.domain.enums.DonationStatus
import com.example.depi_final_project_bloodbank.domain.enums.RequestStatus
import com.example.depi_final_project_bloodbank.domain.model.BloodRequest
import com.example.depi_final_project_bloodbank.domain.model.DonationLogEntry
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class RequestRepositoryImpl(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance().apply {
        // Enable local cache persistence
        try {
            val settings = com.google.firebase.firestore.FirebaseFirestoreSettings.Builder()
                .setLocalCacheSettings(com.google.firebase.firestore.PersistentCacheSettings.newBuilder().build())
                .build()
            firestoreSettings = settings
        } catch (e: Exception) {
            // Already initialized or other error
        }
    }
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
        val threeDaysAgo = System.currentTimeMillis() - (3 * 24 * 60 * 60 * 1000L)

        requestsCollection
            .whereEqualTo("status", "ACTIVE")
            .get()
            .addOnSuccessListener { snapshot ->
                val requests = snapshot.documents.mapNotNull { doc ->
                    val req = doc.toObject(BloodRequest::class.java)?.copy(id = doc.id)
                    if (req != null && req.createdAt < threeDaysAgo) {
                        requestsCollection.document(doc.id).update("status", "EXPIRED")
                        null
                    } else {
                        req
                    }
                }
                onSuccess(requests.take(10))
            }
            .addOnFailureListener { e -> onFailure(e) }
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

    override suspend fun safeIncrementReservedUnits(id: String, donorId: String, donorName: String, donorBloodType: String): Result<Boolean> {
        return try {
            val docRef = requestsCollection.document(id)
            val notificationRef = firestore.collection("notifications").document()

            // 1. Fetch Request Snapshot for pre-checks
            val snapshot = docRef.get().await()
            if (!snapshot.exists()) throw Exception("Request not found")

            val patientBloodType = snapshot.getString("bloodType") ?: ""
            val currentReserved = snapshot.getLong("unitsReserved") ?: 0L
            val needed = snapshot.getLong("unitsNeeded") ?: 0L
            val userIdOfRequest = snapshot.getString("createdBy") ?: ""

            // 2. Pre-checks (Compatibility & Limit)
            if (!isBloodCompatible(donorBloodType, patientBloodType)) {
                throw Exception("COMPATIBILITY_ERROR")
            }
            if (currentReserved >= needed) {
                throw Exception("Limit reached")
            }

            // 3. Execute Write Batch (Atomic & Fast)
            val batch = firestore.batch()
            batch.update(docRef, "unitsReserved", FieldValue.increment(1))

            val logEntry = DonationLogEntry(
                id = java.util.UUID.randomUUID().toString(),
                donorId = donorId,
                donorName = donorName,
                donorBloodType = donorBloodType,
                status = DonationStatus.PENDING,
                timestamp = System.currentTimeMillis()
            )
            
            batch.update(docRef, "donationLog", FieldValue.arrayUnion(logEntry))
            batch.update(docRef, "donorIds", FieldValue.arrayUnion(donorId))

            val receiverIdValue = userIdOfRequest.ifEmpty { "UNKNOWN_USER" }
            val notificationData = mapOf(
                "id" to notificationRef.id,
                "receiverId" to receiverIdValue,
                "title" to "تبرع جديد! 🩸",
                "body" to "قام مستخدم بالموافقة على طلب التبرع الخاص بك، وهو في انتظار تأكيدك.",
                "createdAt" to System.currentTimeMillis(),
                "type" to "DONATION_RECEIVED",
                "requestId" to id
            )
            batch.set(notificationRef, notificationData)

            batch.commit().await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun isBloodCompatible(donor: String, patient: String): Boolean {
        val d = donor.trim().uppercase()
        val p = patient.trim().uppercase()

        if (d.isEmpty() || p.isEmpty()) return false
        if (d == p) return true
        if (d == "O-") return true
        if (d == "O+") {
            return p == "A+" || p == "B+" || p == "AB+"
        }
        if (d == "A-") {
            return p == "A+" || p == "AB-" || p == "AB+"
        }
        if (d == "A+") {
            return p == "AB+"
        }
        if (d == "B-") {
            return p == "B+" || p == "AB-" || p == "AB+"
        }
        if (d == "B+") {
            return p == "AB+"
        }
        if (d == "AB-") {
            return p == "AB+"
        }
        if (d == "AB+") {
            return false // d == p handled earlier
        }

        return false
    }

    override suspend fun cancelDonation(donationId: String, requestId: String): Result<Boolean> {
        return try {
            val requestRef = requestsCollection.document(requestId)
            val snapshot = requestRef.get().await()
            val bloodRequest = snapshot.toObject(BloodRequest::class.java) ?: throw Exception("Request not found")

            val donorIdToRemove = bloodRequest.donationLog.find { it.id == donationId }?.donorId

            val updatedLog = bloodRequest.donationLog.map { entry ->
                if (entry.id == donationId) entry.copy(status = DonationStatus.CANCELLED)
                else entry
            }

            val currentReserved = updatedLog.count { it.status == DonationStatus.PENDING }
            
            val batch = firestore.batch()
            batch.update(requestRef, "donationLog", updatedLog)
            batch.update(requestRef, "unitsReserved", currentReserved)
            
            // If the user has no more active/pending donations in this request, remove them from donorIds
            val stillDonating = updatedLog.any { it.donorId == donorIdToRemove && it.status != DonationStatus.CANCELLED }
            if (!stillDonating && donorIdToRemove != null) {
                batch.update(requestRef, "donorIds", FieldValue.arrayRemove(donorIdToRemove))
            }

            batch.commit().await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun confirmDonation(donationId: String, requestId: String): Result<Boolean> {
        return try {
            val requestRef = requestsCollection.document(requestId)
            val snapshot = requestRef.get().await()
            val bloodRequest = snapshot.toObject(BloodRequest::class.java) ?: throw Exception("Request not found")

            val updatedLog = bloodRequest.donationLog.map { entry ->
                if (entry.id == donationId) entry.copy(status = DonationStatus.CONFIRMED)
                else entry
            }

            val currentConfirmed = updatedLog.count { it.status == DonationStatus.CONFIRMED }
            val currentReserved = updatedLog.count { it.status == DonationStatus.PENDING }

            val batch = firestore.batch()
            batch.update(requestRef, "donationLog", updatedLog)
            batch.update(requestRef, "unitsConfirmed", currentConfirmed)
            batch.update(requestRef, "unitsReserved", currentReserved)

            if (currentConfirmed >= bloodRequest.unitsNeeded) {
                batch.update(requestRef, "status", RequestStatus.COMPLETED.name)
            }

            batch.commit().await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUnitsNeeded(requestId: String, newUnits: Int): Result<Boolean> {
        return try {
            firestore.collection("requests").document(requestId)
                .update("unitsNeeded", newUnits.toLong())
                .await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun cancelRequest(requestId: String): Result<Boolean> {
        return try {
            requestsCollection.document(requestId)
                .update("status", RequestStatus.CANCELLED.name)
                .await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getRequestsByDonor(donorId: String): Flow<List<BloodRequest>> = callbackFlow {
        val query = requestsCollection.whereArrayContains("donorIds", donorId)
        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val requests = snapshot?.documents?.mapNotNull { it.toObject(BloodRequest::class.java) } ?: emptyList()
            trySend(requests)
        }
        awaitClose { listener.remove() }
    }
}