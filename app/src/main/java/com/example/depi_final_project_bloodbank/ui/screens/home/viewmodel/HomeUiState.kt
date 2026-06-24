package com.example.depi_final_project_bloodbank.ui.screens.home.viewmodel

import com.example.depi_final_project_bloodbank.domain.model.BloodRequest

data class HomeUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val userName: String = "",
    val bloodType: String = "",
    val daysElapsed: Int = 0,
    val nextDonationDate: String = "—",
    val lastDonationDate: String = "—",
    val isAvailableForDonation: Boolean = true,
    val urgentRequests: List<BloodRequest> = emptyList(),
    val error: String? = null
) {
    val canDonateNow: Boolean
        get() = lastDonationDate == "—" || (90 - daysElapsed) <= 0

    val filteredRequests: List<BloodRequest>
        get() = urgentRequests
}