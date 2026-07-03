package com.example.depi_final_project_bloodbank.domain.model

import com.example.depi_final_project_bloodbank.domain.enums.DonationStatus

data class Donation(
    val id: String = "",
    val donorId: String = "",
    val requestId: String = "",
    val status: DonationStatus = DonationStatus.PENDING,
    val units: Int = 1,
    val createdAt: Long = 0L,
    val confirmedAt: Long? = null
)