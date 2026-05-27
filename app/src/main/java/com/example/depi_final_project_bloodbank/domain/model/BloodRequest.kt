package com.example.depi_final_project_bloodbank.domain.model

data class BloodRequest(
    val bloodType: String = "O+",
    val units: Int = 2,
    val hospitalName: String = "",
    val city: String = "",
    val urgency: String = "Very Urgent",
    val contactName: String = "",
    val contactPhone: String = "",
    val doctorNote: String = "",
    val timestamp: Long = System.currentTimeMillis()
)