package com.example.depi_final_project_bloodbank.ui.screens.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.depi_final_project_bloodbank.domain.enums.RequestStatus
import com.example.depi_final_project_bloodbank.data.repository.RequestRepository
import com.example.depi_final_project_bloodbank.domain.model.Donation
import com.example.depi_final_project_bloodbank.domain.model.BloodRequest
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RequestsViewModel(
    private val requestRepository: RequestRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RequestsUiState())
    val uiState = _uiState.asStateFlow()

    private val auth = FirebaseAuth.getInstance()

    init {
        observeOrders()
    }

    private fun observeOrders() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            requestRepository.getAllRequests()
                .distinctUntilChanged()
                .catch { _uiState.update { it.copy(isLoading = false) } }
                .collect { realOrders ->
                    _uiState.update { it.copy(
                        orders = realOrders,
                        isLoading = false,
                        isRefreshing = false
                    ) }
                }
        }
    }

    fun donateToRequest(request: BloodRequest) {
        val currentUserUid = auth.currentUser?.uid ?: return

        // 1. حماية الـ UI: منع الضغط المتكرر على نفس الطلب أثناء المعالجة
        if (_uiState.value.donatingRequestIds.contains(request.id)) return

        // 2. حماية مبدئية: التأكد من وجود مكان متاح
        if (request.unitsReserved >= request.unitsNeeded) return

        viewModelScope.launch {
            // تحديث الـ UI لإظهار حالة التحميل لهذا الطلب تحديداً
            _uiState.update { it.copy(donatingRequestIds = it.donatingRequestIds + request.id) }

            try {
                // 3. العملية الذرية (Atomic Transaction):
                // تشمل: التحقق من الليميت + زيادة العداد + إنشاء التبرع + إرسال الإشعار
                val result = requestRepository.safeIncrementReservedUnits(request.id, currentUserUid)

                if (result.isSuccess) {
                    println("Donation successful: Transaction completed atomically.")
                } else {
                    println("Donation failed: ${result.exceptionOrNull()?.message}")
                }
            } catch (e: Exception) {
                println("Donation error: ${e.message}")
            } finally {
                // إزالة الطلب من قائمة المعالجة
                _uiState.update { it.copy(donatingRequestIds = it.donatingRequestIds - request.id) }
            }
        }
    }

    fun setTab(status: RequestStatus) {
        _uiState.update { it.copy(selectedTab = status) }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun refreshOrders() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            _uiState.update { it.copy(isRefreshing = false) }
        }
    }
}