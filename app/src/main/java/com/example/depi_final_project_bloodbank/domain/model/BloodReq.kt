package com.example.depi_final_project_bloodbank.domain.model

import androidx.annotation.StringRes

data class BloodReq(
    val id: Int = 0,
    val bloodType: String = "A+",
    @StringRes val hospital: Int = 0,
    @StringRes val date: Int = 0,
    val units: Int = 1,
    val progress: Float = 0f,
    val status: RequestStatus = RequestStatus.IN_PROGRESS,
    val donorsCount: Int = 0,
    val isUrgent: Boolean = false,
    val hospitalName: String = "",
    val city: String = "",
    val urgency: String = "Routine",
    val contactName: String = "",
    val contactPhone: String = "",
    val timestamp: Long = 0L
)
