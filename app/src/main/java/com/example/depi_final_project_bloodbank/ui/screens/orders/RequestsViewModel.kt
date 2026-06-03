package com.example.depi_final_project_bloodbank.ui.screens.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.depi_final_project_bloodbank.R
import com.example.depi_final_project_bloodbank.domain.enums.RequestPriority
import com.example.depi_final_project_bloodbank.domain.model.BloodRequest
import com.example.depi_final_project_bloodbank.domain.enums.RequestStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RequestsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(RequestsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        _uiState.update { it.copy(orders = getDummyData()) }
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
            delay(2000)
            _uiState.update {
                it.copy(
                    orders = getDummyData(),
                    isRefreshing = false
                )
            }
        }
    }

    /*
 * TODO:
 * Temporary dummy data.
 * Replace with RequestRepository + Firestore data source.
 */
    private fun getDummyData(): List<BloodRequest> = listOf(
        BloodRequest(
            id = "req1",
            createdBy = "user1",
            bloodType = "A+",
            unitsNeeded = 3,
            unitsReserved = 1,
            unitsConfirmed = 0,
            hospitalName = "Sohag General Hospital",
            governorate = "SOHAG",
            city = "AKHMIM",
            hospitalLat = 26.5590,
            hospitalLng = 31.6948,
            contactName = "Mohamed",
            contactPhone = "01000000000",
            status = RequestStatus.ACTIVE,
            priority = RequestPriority.URGENT,
            createdAt = System.currentTimeMillis()
        ),
        BloodRequest(
            id = "req2",
            createdBy = "user2",
            bloodType = "O-",
            unitsNeeded = 2,
            unitsReserved = 0,
            unitsConfirmed = 0,
            hospitalName = "Akhmim Central Hospital",
            governorate = "SOHAG",
            city = "AKHMIM",
            hospitalLat = 26.5652,
            hospitalLng = 31.7441,
            contactName = "Ahmed",
            contactPhone = "01000000001",
            status = RequestStatus.ACTIVE,
            priority = RequestPriority.NORMAL,
            createdAt = System.currentTimeMillis()
        ),
        BloodRequest(
            id = "req3",
            createdBy = "user3",
            bloodType = "B+",
            unitsNeeded = 2,
            unitsReserved = 2,
            unitsConfirmed = 2,
            hospitalName = "University Hospital",
            governorate = "SOHAG",
            city = "SOHAG",
            hospitalLat = 26.5500,
            hospitalLng = 31.6900,
            contactName = "Ali",
            contactPhone = "01000000002",
            status = RequestStatus.COMPLETED,
            priority = RequestPriority.NORMAL,
            createdAt = System.currentTimeMillis()
        ),
        BloodRequest(
            id = "req4",
            createdBy = "user4",
            bloodType = "AB+",
            unitsNeeded = 4,
            unitsReserved = 1,
            unitsConfirmed = 0,
            hospitalName = "Sohag Heart Hospital",
            governorate = "SOHAG",
            city = "SOHAG",
            hospitalLat = 26.5570,
            hospitalLng = 31.6920,
            contactName = "Omar",
            contactPhone = "01000000003",
            status = RequestStatus.CANCELLED,
            priority = RequestPriority.NORMAL,
            createdAt = System.currentTimeMillis() - 86400000L
        ),
        BloodRequest(
            id = "req5",
            createdBy = "user5",
            bloodType = "O+",
            unitsNeeded = 3,
            unitsReserved = 0,
            unitsConfirmed = 0,
            hospitalName = "Tahta General Hospital",
            governorate = "SOHAG",
            city = "TAHTA",
            hospitalLat = 26.7700,
            hospitalLng = 31.5000,
            contactName = "Karim",
            contactPhone = "01000000004",
            status = RequestStatus.EXPIRED,
            priority = RequestPriority.URGENT,
            createdAt = System.currentTimeMillis() - 172800000L
        )
    )
}
