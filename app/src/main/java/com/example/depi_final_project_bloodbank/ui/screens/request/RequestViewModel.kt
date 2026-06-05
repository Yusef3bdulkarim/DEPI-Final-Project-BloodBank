package com.example.depi_final_project_bloodbank.ui.screens.request

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.depi_final_project_bloodbank.domain.model.BloodRequest
import com.example.depi_final_project_bloodbank.domain.repository.RequestRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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

    fun publish() {
        val current = _request.value

        // التحقق من الموقع الإجباري
        if (!_locationSuccess.value) {
            _error.value = "LOCATION_REQUIRED"
            return
        }

        // التحقق من البيانات
        if (current.hospitalName.isBlank() || current.city.isBlank() || current.contactPhone.isBlank()) {
            _error.value = "REQUIRED"
            return
        }

        val isNumeric = current.contactPhone.all { it.isDigit() }
        if (!isNumeric || current.contactPhone.length < 11) {
            _error.value = "INVALID_PHONE"
            return
        }

        _error.value = null
        _isLoading.value = true

        val finalRequest = current.copy(createdAt = System.currentTimeMillis())

        viewModelScope.launch {
            val result = repository.createRequest(finalRequest)
            result.onSuccess {
                _isLoading.value = false
                _isSuccess.value = true
            }.onFailure { exception ->
                _isLoading.value = false
                _error.value = exception.message ?: "Error"
            }
        }
    }
}