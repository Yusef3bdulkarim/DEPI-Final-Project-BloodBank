package com.example.depi_final_project_bloodbank.domain.model

import com.example.depi_final_project_bloodbank.domain.enums.RequestPriority
import com.example.depi_final_project_bloodbank.domain.enums.RequestStatus

data class BloodRequest(
    val id: String = "",
    val createdBy: String = "",
    val bloodType: String = "",
    val unitsNeeded: Int = 1, //المطلوب
    val unitsReserved: Int = 0,//الي وافقوا مبدأيا
    val unitsConfirmed: Int = 0,//الي اكدوا انهم تبرعوا فعلا
    val hospitalName: String = "",
    val governorate: String = "",
    val city: String = "",
    val hospitalLat: Double = 0.0,// هنستخدمه ممع جوجل ماب intent
    val hospitalLng: Double = 0.0,//هنستخدمه ممع جوجل ماب intent
    val contactName: String = "",
    val contactPhone: String = "",
    val status: RequestStatus = RequestStatus.ACTIVE,
    val priority: RequestPriority = RequestPriority.NORMAL,
    val createdAt: Long = 0L,
    val expiresAt: Long? = null,
    val donorIds: List<String> = emptyList(),
    val donationLog: List<DonationLogEntry> = emptyList()
)