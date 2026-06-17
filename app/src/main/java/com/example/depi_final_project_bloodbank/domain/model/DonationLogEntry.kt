package com.example.depi_final_project_bloodbank.domain.model

import com.example.depi_final_project_bloodbank.domain.enums.DonationStatus

data class DonationLogEntry(
    val id: String = "",
    val donorId: String = "",
    val donorName: String = "",
    val donorBloodType: String = "",
    val units: Int = 1,
    val status: DonationStatus = DonationStatus.PENDING,
    val timestamp: Long = System.currentTimeMillis()
)
