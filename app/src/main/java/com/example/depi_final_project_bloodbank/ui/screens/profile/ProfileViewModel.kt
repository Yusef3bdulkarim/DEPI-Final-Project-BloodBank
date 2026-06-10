package com.example.depi_final_project_bloodbank.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.depi_final_project_bloodbank.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    // تعريف الـ Repository اللي عملناه
    private val repository = UserRepository()

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        // أول ما الـ ViewModel يشتغل، هيروح يجيب الداتا فوراً
        fetchUserData()
    }

    private fun fetchUserData() {
        viewModelScope.launch {
            val user = repository.getCurrentUser()
            if (user != null) {
                // لقينا اليوزر، هنحدث الـ State بالبيانات الحقيقية
                _uiState.value = _uiState.value.copy(
                    name = user.name,
                    location = user.governorate,
                    bloodType = user.bloodType
                )
            } else {
                // لو حصل مشكلة أو ملقاش اليوزر
                _uiState.value = _uiState.value.copy(
                    name = "مستخدم غير معروف"
                )
            }
        }
    }
}