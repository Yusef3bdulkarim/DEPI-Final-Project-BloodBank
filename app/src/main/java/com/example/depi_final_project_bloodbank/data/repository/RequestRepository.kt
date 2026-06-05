package com.example.depi_final_project_bloodbank.domain.repository

import com.example.depi_final_project_bloodbank.domain.enums.RequestStatus
import com.example.depi_final_project_bloodbank.domain.model.BloodRequest
import kotlinx.coroutines.flow.Flow

interface RequestRepository {
    // دالة لإنشاء طلب جديد
    suspend fun createRequest(request: BloodRequest): Result<Boolean>

    // دالة لجلب الطلبات بناءً على المدينة (عشان الإشعارات والفلترة)
    fun getRequestsByCity(city: String): Flow<List<BloodRequest>>

    // دالة لجلب تفاصيل طلب معين بالـ ID
    suspend fun getRequestById(id: String): Result<BloodRequest?>

    // دالة لتغيير حالة الطلب (مثلا من ACTIVE لـ COMPLETED)
    suspend fun updateRequestStatus(id: String, status: RequestStatus): Result<Boolean>

    // دالة لزيادة عدد الوحدات المحجوزة
    suspend fun incrementReservedUnits(id: String): Result<Boolean>

    // دالة لزيادة عدد الوحدات المؤكدة (اللي اتبرعوا بيها فعلا)
    suspend fun incrementConfirmedUnits(id: String): Result<Boolean>
}