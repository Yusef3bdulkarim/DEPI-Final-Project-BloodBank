package com.example.depi_final_project_bloodbank.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.depi_final_project_bloodbank.data.repository.UserRepository
import com.example.depi_final_project_bloodbank.data.repository.DonationRepository // 1. زيادة سطر الـ Import ده
import com.google.firebase.auth.FirebaseAuth // 2. زيادة سطر الـ Import ده
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest // 3. زيادة سطر الـ Import ده
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val repository = UserRepository()
    private val donationRepository = DonationRepository() // 4. زيادة السطر ده هنا

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        fetchUserData() // الكود القديم بتاعك زي ما هو
        observeLiveDonationsCount() // 5. زيادة استدعاء الدالة الجديدة هنا
    }

    // دالتك القديمة زي ما هي مش بنلمسها
    private fun fetchUserData() {
        viewModelScope.launch {
            val user = repository.getCurrentUser()
            if (user != null) {
                _uiState.value = _uiState.value.copy(
                    name = user.name,
                    location = user.governorate,
                    bloodType = user.bloodType
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    name = "مستخدم غير معروف"
                )
            }
        }
    }

    // 6. زيادة الدالة الجديدة دي بالكامل في الآخر
    private fun observeLiveDonationsCount() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        viewModelScope.launch {
            donationRepository.observeTotalConfirmedDonations(currentUserId).collectLatest { count ->
                _uiState.value = _uiState.value.copy(
                    totalDonations = count
                )
            }
        }
    }
}