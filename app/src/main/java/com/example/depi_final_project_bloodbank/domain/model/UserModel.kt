package com.example.depi_final_project_bloodbank.domain.model

import java.time.LocalDate

data class User(
    val name: String,
    val bloodType: String,
    val lastDonationDate: LocalDate,
    val profileImageUrl: String? = null
)