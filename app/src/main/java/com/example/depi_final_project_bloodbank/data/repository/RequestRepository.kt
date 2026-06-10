package com.example.depi_final_project_bloodbank.data.repository

import com.example.depi_final_project_bloodbank.domain.model.BloodRequest
import com.google.firebase.firestore.FirebaseFirestore

class RequestRepository {
    private val firestore = FirebaseFirestore.getInstance()

    fun getActiveRequests(
        onSuccess: (List<BloodRequest>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firestore.collection("Requests")
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
}