package com.example.depi_final_project_bloodbank.ui.screens.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.depi_final_project_bloodbank.data.repository.RequestRepository
import com.example.depi_final_project_bloodbank.data.repository.UserRepository
import com.example.depi_final_project_bloodbank.utils.seedBloodRequests
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//package com.example.depi_final_project_bloodbank.ui.screens.home.viewmodel
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.depi_final_project_bloodbank.data.repository.UserRepository
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.launch
//
//// 1. حالة الشاشة (البيانات اللي هتتعرض)
//data class HomeUiState(
//    val userName: String = "جاري التحميل...",
//    val bloodType: String = "-"
//)
//
//class HomeViewModel : ViewModel() {
//    private val repository = UserRepository()
//
//    private val _uiState = MutableStateFlow(HomeUiState())
//    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
//
//    init {
//        fetchUserData()
//    }
//
//    private fun fetchUserData() {
//        viewModelScope.launch {
//            val user = repository.getCurrentUser()
//            if (user != null) {
//                // لو لقينا اليوزر، بنحدث الاسم وفصيلة الدم
//                _uiState.value = _uiState.value.copy(
//                    userName = user.name,
//                    bloodType = user.bloodType
//                )
//            } else {
//                _uiState.value = _uiState.value.copy(
//                    userName = "مستخدم"
//                )
//            }
//        }
//    }
//}
class HomeViewModel : ViewModel() {

    private val userRepository = UserRepository()
    private val requestRepository = RequestRepository()

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val donationIntervalDays = 90L

    init {
        // TEMPORARY — remove after seeding once
        seedBloodRequests()
        loadData()
    }

    fun loadData(isRefresh: Boolean = false) {
        _uiState.update {
            if (isRefresh) it.copy(isRefreshing = true, error = null)
            else it.copy(isLoading = true, error = null)
        }

        // تحويل المناداة لـ CoroutineScope لتتوافق مع الـ Repository الجديد
        viewModelScope.launch {
            try {
                val user = userRepository.getCurrentUser() // نداء مباشر نَظيف بدون Callbacks

                val lastTs = user?.lastDonationDate

                val daysElapsed = lastTs?.let {
                    ((System.currentTimeMillis() - it) / (1000 * 60 * 60 * 24))
                        .toInt().coerceAtLeast(0)
                } ?: 0

                val nextDonationDate = lastTs?.let {
                    dateFormat.format(Date(it + donationIntervalDays * 24 * 60 * 60 * 1000))
                } ?: "—"

                val lastDonationDate = lastTs?.let {
                    dateFormat.format(Date(it))
                } ?: "—"

                _uiState.update {
                    it.copy(
                        userName = user?.name ?: "",
                        bloodType = user?.bloodType ?: "",
                        daysElapsed = daysElapsed,
                        nextDonationDate = nextDonationDate,
                        lastDonationDate = lastDonationDate,
                        isAvailableForDonation = user?.isAvailableForDonation ?: true
                    )
                }

                // تحميل الطلبات بعد النجاح
                loadRequests()

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, isRefreshing = false, error = e.message)
                }
            }
        }
    }

    private fun loadRequests() {
        requestRepository.getActiveRequests(
            onSuccess = { requests ->
                _uiState.update { it.copy(isLoading = false, isRefreshing = false, urgentRequests = requests) }
            },
            onFailure = { e ->
                _uiState.update { it.copy(isLoading = false, isRefreshing = false, error = e.message) }
            }
        )
    }

    fun recordDonation() {
        val now = System.currentTimeMillis()
        userRepository.updateLastDonationDate(
            timestamp = now,
            onSuccess = {
                _uiState.update {
                    it.copy(
                        daysElapsed = 0,
                        nextDonationDate = dateFormat.format(Date(now + donationIntervalDays * 24 * 60 * 60 * 1000)),
                        lastDonationDate = dateFormat.format(Date(now))
                    )
                }
            },
            onFailure = { e ->
                _uiState.update { it.copy(error = e.message) }
            }
        )
    }

    fun toggleAvailability() {
        val newValue = !_uiState.value.isAvailableForDonation
        _uiState.update { it.copy(isAvailableForDonation = newValue) }
        userRepository.updateAvailability(
            isAvailable = newValue,
            onSuccess = {},
            onFailure = { _uiState.update { it.copy(isAvailableForDonation = !newValue) } }
        )
    }

    fun onBloodTypeFilterChanged(type: String) {
        _uiState.update { it.copy(selectedBloodTypeFilter = type) }
    }
}