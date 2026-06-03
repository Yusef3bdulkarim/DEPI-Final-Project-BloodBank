package com.example.depi_final_project_bloodbank.domain.model

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val bloodType: String = "",
    val governorate: String = "",
    val city: String = "",
    val fcmToken: String = "",
    val lastDonationDate: Long? = null,
    val isAvailableForDonation: Boolean = true,
    val createdAt: Long = 0L
)