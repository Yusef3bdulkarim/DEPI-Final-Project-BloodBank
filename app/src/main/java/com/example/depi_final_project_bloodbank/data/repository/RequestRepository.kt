package com.example.depi_final_project_bloodbank.data.repository

import com.example.depi_final_project_bloodbank.domain.enums.RequestStatus
import com.example.depi_final_project_bloodbank.domain.model.BloodRequest
import kotlinx.coroutines.flow.Flow

interface RequestRepository {
    suspend fun createRequest(request: BloodRequest): Result<Boolean>
    fun getActiveRequests(onSuccess: (List<BloodRequest>) -> Unit, onFailure: (Exception) -> Unit)
    fun getAllRequests(): Flow<List<BloodRequest>>
    fun getRequestsByCity(city: String): Flow<List<BloodRequest>>
    suspend fun getRequestById(id: String): Result<BloodRequest?>
    suspend fun updateRequestStatus(id: String, status: RequestStatus): Result<Boolean>
    suspend fun incrementReservedUnits(id: String): Result<Boolean>
    suspend fun incrementConfirmedUnits(id: String): Result<Boolean>
    suspend fun safeIncrementReservedUnits(id: String, donorId: String, donorName: String, donorBloodType: String): Result<Boolean>
    suspend fun cancelDonation(donationId: String, requestId: String): Result<Boolean>
    suspend fun cancelRequest(requestId: String): Result<Boolean>
    suspend fun confirmDonation(donationId: String, requestId: String): Result<Boolean>
    suspend fun updateUnitsNeeded(requestId: String, newUnits: Int): Result<Boolean>
    fun getRequestsByDonor(donorId: String): Flow<List<BloodRequest>>
}