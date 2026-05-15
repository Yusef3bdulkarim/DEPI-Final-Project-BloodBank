package com.example.depi_final_project_bloodbank.ui.screens.profile

data class ProfileUiState(
    val name: String = "Ahmed Mohamed",
    val location: String = "Cairo, Egypt",
    val bloodType: String = "O+",
    val totalDonations: Int = 14,
    val lastDonationDate: String = "12 May",
    val badges: List<Badge> = listOf(
        Badge("Expert", "expert"),
        Badge("Life Saver", "life"),
        Badge("First Year", "star")
    ),
    val nextAppointmentDays: Int = 4
)

data class Badge(
    val title: String,
    val type: String
)