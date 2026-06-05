package com.example.depi_final_project_bloodbank.ui.screens.request

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import com.example.depi_final_project_bloodbank.domain.model.BloodRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class RequestViewModel : ViewModel() {
    private val _request = MutableStateFlow(BloodRequest())
    val request = _request.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    // متغيرات الـ Location
    private val _locationLoading = MutableStateFlow(false)
    val locationLoading = _locationLoading.asStateFlow()

    private val _locationSuccess = MutableStateFlow(false)
    val locationSuccess = _locationSuccess.asStateFlow()

    fun updateRequest(req: BloodRequest) {
        _request.value = req
        _error.value = null
    }

    @SuppressLint("MissingPermission") // الصلاحيات متأمنة في شاشة الـ UI
    fun fetchCurrentLocation(fusedLocationClient: FusedLocationProviderClient) {
        _locationLoading.value = true
        _locationSuccess.value = false

        val cancellationToken = CancellationTokenSource()

        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            cancellationToken.token
        ).addOnSuccessListener { location ->
            if (location != null) {
                updateRequest(
                    _request.value.copy(
                        hospitalLat = location.latitude,
                        hospitalLng = location.longitude
                    )
                )
                _locationSuccess.value = true
            } else {
                _locationSuccess.value = false
            }
            _locationLoading.value = false
        }.addOnFailureListener {
            _locationSuccess.value = false
            _locationLoading.value = false
        }
    }

    fun publish(): Boolean {
        val current = _request.value

        // 1. فحص الخانات الفارغة
        if (current.hospitalName.isBlank() || current.city.isBlank() || current.contactPhone.isBlank()) {
            _error.value = "REQUIRED"
            return false
        }

        // 2. فحص رقم الهاتف (أرقام فقط + 11 رقم على الأقل)
        val isNumeric = current.contactPhone.all { it.isDigit() }
        if (!isNumeric || current.contactPhone.length < 11) {
            _error.value = "INVALID_PHONE"
            return false
        }

        // 3. لو كله تمام، حدث الوقت للرفع
        _request.value = current.copy(createdAt = System.currentTimeMillis())
        _error.value = null
        return true
    }
}