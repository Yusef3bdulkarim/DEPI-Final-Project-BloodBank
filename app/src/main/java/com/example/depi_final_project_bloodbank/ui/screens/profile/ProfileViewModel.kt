package com.example.depi_final_project_bloodbank.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.depi_final_project_bloodbank.R
import com.example.depi_final_project_bloodbank.data.repository.UserRepository
import com.example.depi_final_project_bloodbank.data.repository.DonationRepository // 1. زيادة سطر الـ Import ده
import com.google.firebase.auth.FirebaseAuth // 2. زيادة سطر الـ Import ده
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest // 3. زيادة سطر الـ Import ده
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


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
    // 1. أضف هذه الـ Imports في أعلى الملف


// 2. داخل الكلاس ProfileViewModel، قم بتعديل دالة fetchUserData وأضف دالة التنسيق:

    private fun fetchUserData() {
        viewModelScope.launch {
            val user = repository.getCurrentUser()
            if (user != null) {
                _uiState.value = _uiState.value.copy(
                    name = user.name,
                    location = "${user.governorate} , ${user.city}",
                    bloodType = user.bloodType,
                    // أضف هذا السطر لتحديث التاريخ في الـ UI
                    lastDonationDate = formatTimestamp(user.lastDonationDate),
                    nextAppointmentDays = calculateRemainingDays(user.lastDonationDate)
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    name = "مستخدم غير معروف"
                )
            }
        }
    }
    private fun calculateRemainingDays(lastDonationTimestamp: Long?): Int {
        if (lastDonationTimestamp == null || lastDonationTimestamp == 0L) return 0

        val ninetyDaysInMillis = 91L * 24 * 60 * 60 * 1000 // تحويل 90 يوم لملي ثانية
        val nextAvailableDate = lastDonationTimestamp + ninetyDaysInMillis
        val currentTime = System.currentTimeMillis()

        // إذا مر 90 يوم أو أكثر، الأيام المتبقية هي 0
        if (currentTime >= nextAvailableDate) return 0

        // حساب الفرق بين الموعد القادم والآن وتحويله لأيام
        val diffInMillis = nextAvailableDate - currentTime
        return (diffInMillis / (1000 * 60 * 60 * 24)).toInt()
    }

    // 3. أضف هذه الدالة المساعدة لتحويل الوقت إلى نص
    private fun formatTimestamp(timestamp: Long?): String {
        if (timestamp == null || timestamp == 0L) return ""
        return try {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            sdf.format(Date(timestamp))
        } catch (e: Exception) {
            ""
        }
    }


    // 6. زيادة الدالة الجديدة دي بالكامل في الآخر
    private fun observeLiveDonationsCount() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        viewModelScope.launch {
            donationRepository.observeTotalConfirmedDonations(currentUserId).collectLatest { count ->
                _uiState.value = _uiState.value.copy(
                    totalDonations = count,
                    badges = calculateBadges(count)
                )
            }
        }
    }
    private fun calculateBadges(totalDonations: Int): List<Badge> {
        return buildList {

            if (totalDonations >= 1) {
                add(Badge(R.string.badge_life_saver, "life"))
            }

            if (totalDonations >= 10) {
                add(Badge(R.string.badge_expert, "expert"))
            }

            add(Badge(R.string.badge_first_year, "star"))
        }
    }
}