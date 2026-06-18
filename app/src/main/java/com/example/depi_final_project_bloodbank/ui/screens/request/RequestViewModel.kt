package com.example.depi_final_project_bloodbank.ui.screens.request

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.depi_final_project_bloodbank.domain.model.BloodRequest
import com.example.depi_final_project_bloodbank.data.repository.RequestRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale

class RequestViewModel(
    private val repository: RequestRepository
) : ViewModel() {

    private val _request = MutableStateFlow(BloodRequest())
    val request = _request.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _locationLoading = MutableStateFlow(false)
    val locationLoading = _locationLoading.asStateFlow()

    private val _locationSuccess = MutableStateFlow(false)
    val locationSuccess = _locationSuccess.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isSuccess = MutableStateFlow(false)
    val isSuccess = _isSuccess.asStateFlow()

    fun updateRequest(req: BloodRequest) {
        _request.value = req
        _error.value = null
    }

    @SuppressLint("MissingPermission")
    fun fetchCurrentLocation(context: Context, fusedLocationClient: FusedLocationProviderClient) {
        _locationLoading.value = true
        _locationSuccess.value = false

        val cancellationToken = CancellationTokenSource()

        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            cancellationToken.token
        ).addOnSuccessListener { location ->
            if (location != null) {
                // جلب العنوان من الإحداثيات في مسار خلفي عشان ميهنجش التطبيق
                viewModelScope.launch(Dispatchers.IO) {
                    try {
                        val geocoder = Geocoder(context, Locale("en", "EG")) // أو "ar" حسب لغة الداتا عندك
                        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)

                        var govName = ""
                        var cityName = ""

                        if (!addresses.isNullOrEmpty()) {
                            val address = addresses[0]
                            govName = address.adminArea ?: ""
                            cityName = address.locality ?: address.subAdminArea ?: ""

                            // تنظيف الكلمة عشان تتطابق مع الـ Dropdown
                            govName = govName.replace(" Governorate", "").replace("محافظة ", "")
                        }

                        // التحديث النهائي للبيانات
                        _request.value = _request.value.copy(
                            hospitalLat = location.latitude,
                            hospitalLng = location.longitude,
                            governorate = govName.ifBlank { _request.value.governorate },
                            city = cityName.ifBlank { _request.value.city }
                        )
                        _locationSuccess.value = true
                        _locationLoading.value = false

                    } catch (e: Exception) {
                        // لو الـ Geocoder فشل (مثلاً مفيش نت)، بنحفظ الإحداثيات بس
                        _request.value = _request.value.copy(
                            hospitalLat = location.latitude,
                            hospitalLng = location.longitude
                        )
                        _locationSuccess.value = true
                        _locationLoading.value = false
                    }
                }
            } else {
                _locationSuccess.value = false
                _locationLoading.value = false
            }
        }.addOnFailureListener {
            _locationSuccess.value = false
            _locationLoading.value = false
        }
    }

    fun publish() {
        val current = _request.value

        // 1. التحقق من لقط الموقع الجغرافي (الـ GPS)
        if (!_locationSuccess.value) {
            _error.value = "LOCATION_REQUIRED"
            return
        }

        // 2. التحقق من البيانات بالكامل (ضفنا الـ governorate عشان تطابق الـ UI)
        if (current.hospitalName.isBlank() ||
            current.governorate.isBlank() ||
            current.city.isBlank() ||
            current.contactPhone.isBlank()) {
            _error.value = "REQUIRED"
            return
        }

        // 3. التحقق من رقم الهاتف
        val isNumeric = current.contactPhone.all { it.isDigit() }
        if (!isNumeric || current.contactPhone.length < 11) {
            _error.value = "INVALID_PHONE"
            return
        }

        _error.value = null
        _isLoading.value = true

        // جلب الـ UID الحالي للمستخدم عشان الحقل الفاضي في الفايربيز
        val currentUserId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""

        val finalRequest = current.copy(
            createdBy = currentUserId,
            createdAt = System.currentTimeMillis()
        )

        viewModelScope.launch {
            val result = repository.createRequest(finalRequest)
            result.onSuccess { requestId ->

                val notificationId = "notif_${System.currentTimeMillis()}"

                val notification = hashMapOf(
                    "id" to notificationId,
                    "title" to "Urgent Blood Request",
                    "message" to "مطلوب متبرعين لفصيلة ${finalRequest.bloodType} في ${finalRequest.city}",
                    "type" to "URGENT_REQUEST",
                    "relatedId" to requestId,
                    "userId" to currentUserId,
                    "isRead" to false,
                    "createdAt" to System.currentTimeMillis(),
                    "location" to finalRequest.city,
                    "blood_type" to finalRequest.bloodType
                )

                FirebaseFirestore.getInstance()
                    .collection("notification")
                    .document(notificationId)
                    .set(notification)
                    .addOnSuccessListener {
                        _isLoading.value = false
                        _isSuccess.value = true
                    }
                    .addOnFailureListener { e ->
                        _isLoading.value = false
                        _error.value = e.message ?: "Notification Error"
                    }
            }
            .onFailure { exception ->
                _isLoading.value = false
                _error.value = exception.message ?: "Error"
            }
        }
    }
}