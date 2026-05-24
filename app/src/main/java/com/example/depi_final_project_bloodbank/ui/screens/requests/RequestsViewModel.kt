package com.example.depi_final_project_bloodbank.ui.screens.requests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.depi_final_project_bloodbank.R
import com.example.depi_final_project_bloodbank.domain.model.BloodRequest
import com.example.depi_final_project_bloodbank.domain.model.RequestStatus
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

    private fun getDummyData(): List<BloodRequest> = listOf(
        BloodRequest(1, "A+", R.string.hosp_king_fahd, R.string.time_30_mins, 3, 0.4f, RequestStatus.URGENT, 5, true),
        BloodRequest(2, "O-", R.string.hosp_king_saud, R.string.time_1_hour, 1, 0.0f, RequestStatus.URGENT, 2, true),
        BloodRequest(3, "B+", R.string.hosp_al_habib, R.string.time_today_10am, 2, 0.5f, RequestStatus.IN_PROGRESS, 8, false),
        BloodRequest(4, "AB+", R.string.hosp_king_abdulaziz, R.string.time_yesterday_9pm, 4, 0.2f, RequestStatus.IN_PROGRESS, 3, false),
        BloodRequest(5, "O+", R.string.hosp_dallah, R.string.time_may_22, 2, 1.0f, RequestStatus.COMPLETED, 12, false),
        BloodRequest(6, "A-", R.string.hosp_care, R.string.time_may_20, 1, 1.0f, RequestStatus.COMPLETED, 10, false),
        BloodRequest(7, "B-", R.string.hosp_military, R.string.time_may_18, 2, 0.0f, RequestStatus.CANCELLED, 0, false),
        BloodRequest(8, "O-", R.string.hosp_mouwasat, R.string.time_may_15, 1, 0.1f, RequestStatus.CANCELLED, 1, false)
    )
}
