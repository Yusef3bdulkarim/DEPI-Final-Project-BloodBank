package com.example.bloodlink.viewmodel

import androidx.lifecycle.ViewModel
import com.example.bloodlink.R
import com.example.bloodlink.model.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()

    data class NeedsProfileCompletion(val uid: String) : AuthState()
    data class Error(val messageId: Int? = null, val messageStr: String? = null) : AuthState()
}


class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)


    private val firestore = FirebaseFirestore.getInstance()

    val authState: StateFlow<AuthState> = _authState


    fun resetState() {
        _authState.value = AuthState.Idle
    }

    // التعديل التاني: نحدث دالة login
    fun login(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            // نبعت الـ ID بتاع الـ String اللي في ملف الموارد
            _authState.value = AuthState.Error(messageId = com.example.bloodlink.R.string.error_empty_fields)
            return
        }

        _authState.value = AuthState.Loading

        auth.signInWithEmailAndPassword(email.trim(), pass.trim())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Success
                } else {
                    // لو فايربيز بعت رسالة إيرور جاهزة، بنعرضها، لو لا بنعرض رسالة عامة من عندنا
                    val exceptionMessage = task.exception?.localizedMessage
                    if (exceptionMessage != null) {
                        _authState.value = AuthState.Error(messageStr = exceptionMessage)
                    } else {
                        _authState.value = AuthState.Error(messageId = com.example.bloodlink.R.string.error_general_login)
                    }
                }
            }
    }

    fun loginWithGoogle(idToken: String) {
        _authState.value = AuthState.Loading

        val credential = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Success
                } else {
                    // شلنا النص الثابت وحطينا مكانه الـ String Resource
                    val exceptionMessage = task.exception?.localizedMessage
                    if (exceptionMessage != null) {
                        _authState.value = AuthState.Error(messageStr = exceptionMessage)
                    } else {
                        // ضيف error_google_login في ملفات الـ strings
                        _authState.value = AuthState.Error(messageId = R.string.error_google_login)
                    }
                }
            }
    }


    fun register(name: String, email: String, phone: String, pass: String, bloodType: String) {

        if (name.isBlank() || email.isBlank() || phone.isBlank() || pass.isBlank() || bloodType.isBlank()) {
            // التعديل هنا: استخدمنا messageId بدل messageResId
            _authState.value = AuthState.Error(messageId = R.string.error_empty_fields)
            return
        }

        _authState.value = AuthState.Loading

        auth.createUserWithEmailAndPassword(email.trim(), pass.trim())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    val uid = task.result?.user?.uid ?: ""

                    val user = User(
                        uid = uid,
                        name = name.trim(),
                        email = email.trim(),
                        phone = phone.trim(),
                        bloodType = bloodType
                    )

                    // نرفع البيانات لـ Firestore
                    firestore.collection("Users").document(uid).set(user)
                        .addOnSuccessListener {
                            _authState.value = AuthState.Success
                        }
                        .addOnFailureListener { e ->
                            // التعديل هنا: استخدمنا messageId
                            _authState.value = AuthState.Error(
                                messageStr = e.localizedMessage,
                                messageId = R.string.error_saving_data
                            )
                        }

                } else {
                    val exceptionMessage = task.exception?.localizedMessage
                    // التعديل هنا: استخدمنا messageId
                    _authState.value = AuthState.Error(
                        messageStr = exceptionMessage,
                        messageId = R.string.error_creating_account
                    )
                }
            }
    }



}