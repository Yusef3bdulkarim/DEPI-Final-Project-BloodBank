package com.example.depi_final_project_bloodbank.ui.screens.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.depi_final_project_bloodbank.R
import com.example.depi_final_project_bloodbank.data.repository.UserRepository
import com.example.depi_final_project_bloodbank.domain.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.example.depi_final_project_bloodbank.data.repository.BloodScannerRepository
import com.example.depi_final_project_bloodbank.data.repository.BloodScannerRepositoryImpl
import com.example.depi_final_project_bloodbank.domain.model.BloodScanResult
import android.graphics.Bitmap
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()

    data class NeedsProfileCompletion(val uid: String) : AuthState()
    object NeedsVerification : AuthState()
    object PasswordResetSent : AuthState()
    data class Error(val messageId: Int? = null, val messageStr: String? = null) : AuthState()
}


class AuthViewModel : ViewModel() {
    private val repository = UserRepository()

    // 1. إضافة الـ Repository المسؤول عن تحليل الصور باستخدام ML Kit و Groq AI
    private val bloodScannerRepository: BloodScannerRepository = BloodScannerRepositoryImpl()

    // 2. حالة (State) لحفظ نتيجة تحليل الصورة ومراقبتها في الـ UI
    private val _scanResult = MutableStateFlow<BloodScanResult?>(null)
    val scanResult: StateFlow<BloodScanResult?> = _scanResult.asStateFlow()

    // 5. حالة لمراقبة عملية التحليل (هل هي جارية الآن؟) لإظهار مؤشر تحميل للمستخدم
    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

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
            _authState.value = AuthState.Error(messageId = R.string.error_empty_fields)
            return
        }

        _authState.value = AuthState.Loading

        auth.signInWithEmailAndPassword(email.trim(), pass.trim())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (auth.currentUser?.isEmailVerified == true) {
                        onUserAuthenticated()
                        _authState.value = AuthState.Success
                    } else {
                        _authState.value = AuthState.NeedsVerification
                    }
                } else {
                    val exception = task.exception

                    // هنا نحدد الـ Resource ID بناءً على نوع الخطأ
                    val resId = when (exception) {
                        is FirebaseAuthInvalidUserException,
                        is FirebaseAuthInvalidCredentialsException -> {
                            R.string.error_invalid_credentials
                        }
                        else -> {
                            // إذا كان خطأ غير معروف، نستخدم الرسالة العامة الموجودة في ملفاتك
                            R.string.error_general_login
                        }
                    }

                    // نمرر الـ messageId بدلاً من messageStr
                    _authState.value = AuthState.Error(messageId = resId)
                }
            }
    }

    fun loginWithGoogle(idToken: String) {
        _authState.value = AuthState.Loading

        val credential = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = task.result?.user?.uid ?: ""

                    // هنا بنسأل Firestore: هل المستخدم ده موجود وليه بيانات؟
                    firestore.collection("Users").document(uid).get()
                        .addOnSuccessListener { document ->
                            if (document.exists() && document.contains("bloodType")) {
                                // مستخدم قديم وبياناته كاملة -> دخله على الشاشة الرئيسية
                                onUserAuthenticated()
                                _authState.value = AuthState.Success
                            } else {
                                // مستخدم جديد (أو بياناته ناقصة) -> وديه يكمل بياناته
                                _authState.value = AuthState.NeedsProfileCompletion(uid)
                            }
                        }
                        .addOnFailureListener {
                            // التعديل هنا: استخدمنا messageId بدل messageStr
                            _authState.value = AuthState.Error(messageId = R.string.error_fetching_data)
                        }
                } else {
                    val exceptionMessage = task.exception?.localizedMessage
                    // التعديل هنا برضه عشان الأرقام متظهرش
                    if (exceptionMessage != null) {
                        _authState.value = AuthState.Error(messageStr = exceptionMessage)
                    } else {
                        _authState.value = AuthState.Error(messageId = R.string.error_google_login)
                    }
                }
            }
    }


    fun completeProfile(uid: String, name: String, phone: String, bloodType: String, governorate: String, city: String, lastDonationDate: Long?) { // شلنا proofImageBytes
        if (name.isBlank() || phone.isBlank() || bloodType.isBlank() || governorate.isBlank() || city.isBlank()) {
            _authState.value = AuthState.Error(messageId = R.string.error_empty_fields)
            return
        }

        _authState.value = AuthState.Loading

        viewModelScope.launch {
            try {

                val user = User(
                    uid = uid,
                    name = name.trim(),
                    email = auth.currentUser?.email ?: "",
                    phone = phone.trim(),
                    bloodType = bloodType,
                    governorate = governorate,
                    city = city,
                    lastDonationDate = lastDonationDate,
                    proofImageUrl = "" // بنسيبها فاضية
                )

                firestore.collection("Users").document(uid).set(user).await()

                onUserAuthenticated()
                _authState.value = AuthState.Success

            } catch (e: Exception) {
                _authState.value = AuthState.Error(
                    messageId = R.string.error_saving_data,
                    messageStr = e.localizedMessage
                )
            }
        }
    }


    fun register(name: String, email: String, phone: String, pass: String, bloodType: String, governorate: String, city: String, lastDonationDate: Long?) { // شلنا proofImageBytes
        if (name.isBlank() || email.isBlank() || phone.isBlank() || pass.isBlank() || bloodType.isBlank() || governorate.isBlank() || city.isBlank()) {
            _authState.value = AuthState.Error(messageId = R.string.error_empty_fields)
            return
        }
        _authState.value = AuthState.Loading

        viewModelScope.launch {
            try {

                val authResult = auth.createUserWithEmailAndPassword(email.trim(), pass.trim()).await()
                val uid = authResult.user?.uid ?: ""

                val user = User(
                    uid = uid,
                    name = name.trim(),
                    email = email.trim(),
                    phone = phone.trim(),
                    bloodType = bloodType,
                    governorate = governorate,
                    city = city,
                    lastDonationDate = lastDonationDate,
                    proofImageUrl = "" // بنسيبها فاضية
                )

                firestore.collection("Users").document(uid).set(user).await()

                onUserAuthenticated()
                auth.currentUser?.sendEmailVerification()
                _authState.value = AuthState.NeedsVerification

            } catch (e: Exception) {
                _authState.value = AuthState.Error(messageStr = e.localizedMessage)
            }
        }
    }

    fun resetPassword(email: String) {
        if (email.isBlank()) {
            _authState.value = AuthState.Error(messageId = R.string.error_empty_fields)
            return
        }
        _authState.value = AuthState.Loading
        auth.sendPasswordResetEmail(email.trim())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.PasswordResetSent
                } else {
                    _authState.value = AuthState.Error(messageStr = task.exception?.localizedMessage)
                }
            }
    }

    // 5. إعادة إرسال إيميل التفعيل (جديد)
    fun resendVerificationEmail() {
        auth.currentUser?.sendEmailVerification()
    }

    // دالة للتحقق المستمر من تفعيل الإيميل
    fun checkEmailVerificationStatus() {
        // بنعمل تحديث لبيانات المستخدم من السيرفر
        auth.currentUser?.reload()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // لو الإيميل اتفعل، بنغير الحالة لـ Success
                if (auth.currentUser?.isEmailVerified == true) {
                    _authState.value = AuthState.Success
                }
            }
        }
    }
//    updateFcmToken
    fun updateFcmToken() {
        viewModelScope.launch {
            repository.updateFcmToken()
        }
    }
    private fun onUserAuthenticated() {
        updateFcmToken()
    }

    // 3. دالة لبدء عملية تحليل فصيلة الدم من الصورة (Bitmap)
    fun scanBloodType(bitmap: Bitmap) {
        viewModelScope.launch {
            // تفعيل حالة التحميل قبل البدء في التحليل
            _isScanning.value = true
            try {
                // نرسل الصورة للـ Repository ونحدث الحالة بالنتيجة (سواء نجاح أو فشل)
                val result = bloodScannerRepository.scanBloodType(bitmap)
                _scanResult.value = result
            } finally {
                // إيقاف حالة التحميل بعد انتهاء العملية (سواء نجحت أو فشلت)
                _isScanning.value = false
            }
        }
    }

    // 4. دالة لتصفير نتيجة التحليل عند الحاجة (مثلاً عند الخروج من الشاشة)
    fun clearScanResult() {
        _scanResult.value = null
    }

}
