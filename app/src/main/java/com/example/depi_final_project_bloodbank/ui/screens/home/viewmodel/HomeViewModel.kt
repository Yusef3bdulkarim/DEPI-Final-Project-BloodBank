package com.example.depi_final_project_bloodbank.ui.screens.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.depi_final_project_bloodbank.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// 1. حالة الشاشة (البيانات اللي هتتعرض)
data class HomeUiState(
    val userName: String = "جاري التحميل...",
    val bloodType: String = "-"
)

class HomeViewModel : ViewModel() {
    private val repository = UserRepository()

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        fetchUserData()
    }

    private fun fetchUserData() {
        viewModelScope.launch {
            val user = repository.getCurrentUser()
            if (user != null) {
                // لو لقينا اليوزر، بنحدث الاسم وفصيلة الدم
                _uiState.value = _uiState.value.copy(
                    userName = user.name,
                    bloodType = user.bloodType
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    userName = "مستخدم"
                )
            }
        }
    }
}