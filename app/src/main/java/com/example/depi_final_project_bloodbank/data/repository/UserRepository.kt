package com.example.depi_final_project_bloodbank.data.repository

import com.example.depi_final_project_bloodbank.domain.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    fun updateLastDonationDate(
        timestamp: Long,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("Users").document(uid)
            .update("lastDonationDate", timestamp)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun updateAvailability(
        isAvailable: Boolean,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("Users").document(uid)
            .update("isAvailableForDonation", isAvailable)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun getCurrentUser(
        onSuccess: (User?) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            onSuccess(null)
            return
        }
        firestore.collection("Users").document(uid)
            .get()
            .addOnSuccessListener { doc ->
                onSuccess(doc.toObject(User::class.java))
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }
}