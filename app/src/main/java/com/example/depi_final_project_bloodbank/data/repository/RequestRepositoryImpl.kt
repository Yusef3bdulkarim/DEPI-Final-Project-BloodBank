package com.example.depi_final_project_bloodbank.data.repository

import com.example.depi_final_project_bloodbank.domain.enums.RequestStatus
import com.example.depi_final_project_bloodbank.domain.model.BloodRequest
import com.example.depi_final_project_bloodbank.domain.repository.RequestRepository
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class RequestRepositoryImpl(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : RequestRepository {

    // بنحدد الكولكشن بتاعنا في الفايربيز
    private val requestsCollection = firestore.collection("requests")

    override suspend fun createRequest(request: BloodRequest): Result<Boolean> {
        return try {
            val docRef = requestsCollection.document() // بنعمل ID جديد
            val finalRequest = request.copy(id = docRef.id) // بنحط الـ ID في الموديل

            docRef.set(finalRequest).await() // بنرفع الداتا
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
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
                    doc.toObject(BloodRequest::class.java)
                }
                trySend(requests).isSuccess
            }
        }
        awaitClose { listener.remove() }
    }

    override suspend fun getRequestById(id: String): Result<BloodRequest?> {
        return try {
            val doc = requestsCollection.document(id).get().await()
            val request = doc.toObject(BloodRequest::class.java)
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
}