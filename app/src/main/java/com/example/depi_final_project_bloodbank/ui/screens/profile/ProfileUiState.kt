package com.example.depi_final_project_bloodbank.ui.screens.profile

import androidx.annotation.StringRes
import com.example.depi_final_project_bloodbank.R

data class ProfileUiState(
    val name: String = "",
    val location: String = "...",
    val bloodType: String = "-",
    val totalDonations: Int = 0,
    val lastDonationDate: String = "",
    val badges: List<Badge> = listOf(
        Badge(R.string.badge_expert, "expert"),
        Badge(R.string.badge_life_saver, "life"),
        Badge(R.string.badge_first_year, "star")
    ),
    val nextAppointmentDays: Int = 0
)

data class Badge(
    @StringRes val titleRes: Int,
    val type: String
)