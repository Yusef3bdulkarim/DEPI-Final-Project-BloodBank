package com.example.depi_final_project_bloodbank.ui.screens.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.depi_final_project_bloodbank.domain.enums.RequestStatus
import com.example.depi_final_project_bloodbank.data.repository.RequestRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RequestsViewModel(
    private val repository: RequestRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RequestsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        observeOrders()
    }

    private fun observeOrders() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // الربط بـ Firestore من خلال الـ Repository
            repository.getAllRequests()
                .catch { e ->
                    // هنا ممكن تتعامل مع الأخطاء لو حابب
                    _uiState.update { it.copy(isLoading = false) }
                }
                .collect { realOrders ->
                    _uiState.update { it.copy(
                        orders = realOrders,
                        isLoading = false,
                        isRefreshing = false // إيقاف الـ Refresh في حال كان شغال
                    ) }
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
        // بما أننا نستخدم SnapshotListener في الـ Repository،
        // التحديث بيتم أوتوماتيك، لكن لو حابب تظهر تأثير الـ Loading للـ Refresh:
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            // الـ Repository هو اللي هيحدث الـ State أوتوماتيك بمجرد وصول داتا جديدة
            _uiState.update { it.copy(isRefreshing = false) }
        }
    }
}