package com.example.depi_final_project_bloodbank.domain.model

import android.net.Uri

sealed class ScanState {
    object Idle : ScanState()
    object Loading : ScanState()
    data class Success(val bloodType: String, val rawText: String, val imageUri: Uri?) : ScanState()
    data class Error(val message: String, val rawText: String? = null) : ScanState()
}

data class BloodScanResult(
    val bloodType: String?,
    val rawText: String,
    val isSuccessful: Boolean,
    val errorMessage: String? = null
)
