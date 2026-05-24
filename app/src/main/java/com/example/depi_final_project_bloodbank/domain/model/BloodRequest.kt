package com.example.depi_final_project_bloodbank.domain.model

import androidx.annotation.StringRes

data class BloodRequest(
    val id: Int,
    val bloodType: String,
    @StringRes val hospital: Int,
    @StringRes val date: Int,
    val unitsNeeded: Int,
    val progress: Float,
    val status: RequestStatus,
    val donorsCount: Int,
    val isUrgent: Boolean
)
