package com.example.depi_final_project_bloodbank.ui.screens.request

import androidx.lifecycle.ViewModel
import com.example.depi_final_project_bloodbank.domain.model.BloodRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class RequestViewModel : ViewModel() {
    private val _request = MutableStateFlow(BloodRequest())
    val request = _request.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun updateRequest(req: BloodRequest) {
        _request.value = req
        _error.value = null
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

        // 3. لو كله تمام، حدث الوقت وانشر
        _request.value = current.copy(createdAt = System.currentTimeMillis())
        _error.value = null
        return true
    }
}
