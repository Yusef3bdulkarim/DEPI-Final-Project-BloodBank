package com.example.depi_final_project_bloodbank.data.repository

import android.graphics.Bitmap
import com.example.depi_final_project_bloodbank.domain.model.BloodScanResult

interface BloodScannerRepository {
    suspend fun scanBloodType(bitmap: Bitmap): BloodScanResult

}