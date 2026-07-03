package com.example.depi_final_project_bloodbank.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.depi_final_project_bloodbank.data.repository.RequestRepositoryImpl
import com.example.depi_final_project_bloodbank.domain.enums.DonationStatus
import com.example.depi_final_project_bloodbank.domain.model.BloodRequest
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class DonationHistoryUiState(
    val donations: List<BloodRequest> = emptyList(),
    val isLoading: Boolean = false
)

class DonationsViewModel : ViewModel() {

    private val requestRepository = RequestRepositoryImpl()

    private val _uiState = MutableStateFlow(DonationHistoryUiState())
    val uiState: StateFlow<DonationHistoryUiState> = _uiState.asStateFlow()

    init {
        observeUserDonations()
    }

    private fun observeUserDonations() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            requestRepository.getRequestsByDonor(currentUserId).collectLatest { requests ->
                _uiState.value = DonationHistoryUiState(
                    donations = requests,
                    isLoading = false
                )
            }
        }
    }

    fun cancelDonation(donationId: String, requestId: String) {
        val previousDonations = _uiState.value.donations
        
        // Optimistic UI: Update the local donation log inside the matched request
        val updatedDonations = previousDonations.map { request ->
            if (request.id == requestId) {
                val updatedLog = request.donationLog.map { entry ->
                    if (entry.id == donationId) entry.copy(status = DonationStatus.CANCELLED)
                    else entry
                }
                request.copy(donationLog = updatedLog)
            } else request
        }
        
        _uiState.value = _uiState.value.copy(donations = updatedDonations)

        viewModelScope.launch {
            try {
                val result = requestRepository.cancelDonation(donationId, requestId)
                if (!result.isSuccess) {
                    _uiState.value = _uiState.value.copy(donations = previousDonations)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(donations = previousDonations)
            }
        }
    }
}
