package com.example.depi_final_project_bloodbank.ui.screens.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.depi_final_project_bloodbank.R
import com.example.depi_final_project_bloodbank.domain.model.BloodReq
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

    private fun getDummyData(): List<BloodReq> = listOf(
        BloodReq(id = 1, bloodType = "A+", hospital = R.string.hosp_king_fahd, date = R.string.time_30_mins, units = 3, progress = 0.4f, status = RequestStatus.URGENT, donorsCount = 5, isUrgent = true),
        BloodReq(id = 2, bloodType = "O-", hospital = R.string.hosp_king_saud, date = R.string.time_1_hour, units = 1, progress = 0.0f, status = RequestStatus.URGENT, donorsCount = 2, isUrgent = true),
        BloodReq(id = 3, bloodType = "B+", hospital = R.string.hosp_al_habib, date = R.string.time_today_10am, units = 2, progress = 0.5f, status = RequestStatus.IN_PROGRESS, donorsCount = 8, isUrgent = false),
        BloodReq(id = 4, bloodType = "AB+", hospital = R.string.hosp_king_abdulaziz, date = R.string.time_yesterday_9pm, units = 4, progress = 0.2f, status = RequestStatus.IN_PROGRESS, donorsCount = 3, isUrgent = false),
        BloodReq(id = 5, bloodType = "O+", hospital = R.string.hosp_dallah, date = R.string.time_may_22, units = 2, progress = 1.0f, status = RequestStatus.COMPLETED, donorsCount = 12, isUrgent = false),
        BloodReq(id = 6, bloodType = "A-", hospital = R.string.hosp_care, date = R.string.time_may_20, units = 1, progress = 1.0f, status = RequestStatus.COMPLETED, donorsCount = 10, isUrgent = false),
        BloodReq(id = 7, bloodType = "B-", hospital = R.string.hosp_military, date = R.string.time_may_18, units = 2, progress = 0.0f, status = RequestStatus.CANCELLED, donorsCount = 0, isUrgent = false),
        BloodReq(id = 8, bloodType = "O-", hospital = R.string.hosp_mouwasat, date = R.string.time_may_15, units = 1, progress = 0.1f, status = RequestStatus.CANCELLED, donorsCount = 1, isUrgent = false)
    )
}
