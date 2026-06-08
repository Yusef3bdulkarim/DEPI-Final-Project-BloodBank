package com.example.depi_final_project_bloodbank.data.repository

import com.example.depi_final_project_bloodbank.domain.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // دالة زمايلك (إحضار بيانات المستخدم باستخدام Coroutines)
    suspend fun getCurrentUser(): User? {
        val uid = auth.currentUser?.uid ?: return null
        return try {
            val document = firestore.collection("Users").document(uid).get().await()
            document.toObject(User::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // دالتك أنت (تحديث تاريخ آخر تبرع)
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

    // دالتك أنت (تحديث حالة الإتاحة للتبرع)
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
}