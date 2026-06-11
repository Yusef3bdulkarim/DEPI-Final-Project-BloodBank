package com.example.depi_final_project_bloodbank.domain.repository

import com.example.depi_final_project_bloodbank.domain.enums.RequestStatus
import com.example.depi_final_project_bloodbank.domain.model.BloodRequest
import kotlinx.coroutines.flow.Flow

interface RequestRepository {
    // --- شغل التيم (عشان شاشاتهم تفضل شغالة) ---
    fun getActiveRequests(
        onSuccess: (List<BloodRequest>) -> Unit,
        onFailure: (Exception) -> Unit
    )

    // --- الشغل بتاعك (Clean Architecture) ---
    suspend fun createRequest(request: BloodRequest): Result<Boolean>
    fun getRequestsByCity(city: String): Flow<List<BloodRequest>>
    suspend fun getRequestById(id: String): Result<BloodRequest?>
    suspend fun updateRequestStatus(id: String, status: RequestStatus): Result<Boolean>
    suspend fun incrementReservedUnits(id: String): Result<Boolean>
    suspend fun incrementConfirmedUnits(id: String): Result<Boolean>
    fun getAllRequests(): Flow<List<BloodRequest>>
}