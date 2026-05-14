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
import com.google.firebase.firestore.SetOptions

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
                        _authState.value = AuthState.Success
                    } else {
                        _authState.value = AuthState.NeedsVerification
                    }
                } else {
                    val exception = task.exception

                    // هنا نحدد الـ Resource ID بناءً على نوع الخطأ
                    val resId = when (exception) {
                        is com.google.firebase.auth.FirebaseAuthInvalidUserException,
                        is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException -> {
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


    fun completeProfile(uid: String, name: String, phone: String, bloodType: String) {
        if (name.isBlank() || phone.isBlank() || bloodType.isBlank()) {
            _authState.value = AuthState.Error(messageId = R.string.error_empty_fields)
            return
        }

        _authState.value = AuthState.Loading

        // التعديل هنا: ضفنا الإيميل والـ uid عشان الملف يتكريت كامل
        val userMap = hashMapOf(
            "uid" to uid,
            "name" to name.trim(),
            "phone" to phone.trim(),
            "bloodType" to bloodType,
            "email" to (auth.currentUser?.email ?: "") // بنجيب إيميل جوجل ونحفظه برضه
        )

        // التعديل الأهم: استخدمنا set مع SetOptions.merge()
        // عشان لو الملف مش موجود يكريته، ولو موجود يضيف عليه
        firestore.collection("Users").document(uid).set(userMap, SetOptions.merge())
            .addOnSuccessListener {
                _authState.value = AuthState.Success
            }
            .addOnFailureListener { e ->
                _authState.value = AuthState.Error(
                    messageId = R.string.error_saving_data,
                    messageStr = e.localizedMessage
                )
            }
    }


    fun register(name: String, email: String, phone: String, pass: String, bloodType: String) {
        if (name.isBlank() || email.isBlank() || phone.isBlank() || pass.isBlank() || bloodType.isBlank()) {
            _authState.value = AuthState.Error(messageId = R.string.error_empty_fields)
            return
        }
        _authState.value = AuthState.Loading
        auth.createUserWithEmailAndPassword(email.trim(), pass.trim())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = task.result?.user?.uid ?: ""
                    val user = User(uid = uid, name = name.trim(), email = email.trim(), phone = phone.trim(), bloodType = bloodType)

                    firestore.collection("Users").document(uid).set(user)
                        .addOnSuccessListener {
                            // إرسال إيميل التفعيل وتوجيه المستخدم لشاشة التفعيل
                            auth.currentUser?.sendEmailVerification()
                            _authState.value = AuthState.NeedsVerification
                        }
                        .addOnFailureListener { e ->
                            _authState.value = AuthState.Error(messageStr = e.localizedMessage)
                        }
                } else {
                    // نعرض رسالة فايربيز الحقيقية (زي: الإيميل مستخدم من قبل)
                    _authState.value = AuthState.Error(messageStr = task.exception?.localizedMessage)
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





}