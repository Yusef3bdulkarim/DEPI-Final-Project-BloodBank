package com.example.depi_final_project_bloodbank.ui.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.depi_final_project_bloodbank.domain.model.BloodRequest
import com.example.depi_final_project_bloodbank.ui.screens.home.components.ActionButtonSection
import com.example.depi_final_project_bloodbank.ui.screens.home.components.AvailabilityToggle
import com.example.depi_final_project_bloodbank.ui.screens.home.components.BloodTypeFilterRow
import com.example.depi_final_project_bloodbank.ui.screens.home.components.DonationCounterBanner
import com.example.depi_final_project_bloodbank.ui.screens.home.components.DonationStatusBanner
import com.example.depi_final_project_bloodbank.ui.screens.home.components.DynamicHealthTipsSection
import com.example.depi_final_project_bloodbank.ui.screens.home.components.HeaderSection
import com.example.depi_final_project_bloodbank.ui.screens.home.components.SectionTitle
import com.example.depi_final_project_bloodbank.ui.screens.home.components.TopLogoBar
import com.example.depi_final_project_bloodbank.ui.screens.home.components.UrgentAppealsList
// استيراد الشيت من مكانه الصحيح اللي بعتهولي في الكود
import com.example.depi_final_project_bloodbank.ui.screens.orders.components.RequestDetailsBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onRequestBloodClick: () -> Unit = {},
    onDonateNowClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onViewRequest: (BloodRequest) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    // ✅ الاعتماد على متغيّر واحد فقط للإظهار والإغلاق لمنع الـ Conflict والشاشة البيضاء
    var selectedRequest by remember { mutableStateOf<BloodRequest?>(null) }

    if (state.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = { viewModel.loadData(isRefresh = true) },
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item { TopLogoBar(onNotificationsClick = onNotificationsClick) }
                item { HeaderSection(state.userName, state.bloodType) }
                item {
                    AvailabilityToggle(
                        isAvailable = state.isAvailableForDonation,
                        onToggle = { viewModel.toggleAvailability() }
                    )
                }
                item {
                    if (state.canDonateNow) {
                        DonationStatusBanner()
                    } else {
                        DonationCounterBanner(
                            daysElapsed = state.daysElapsed,
                            nextDate = state.nextDonationDate,
                            lastDate = state.lastDonationDate
                        )
                    }
                }
                item { DynamicHealthTipsSection() }
                item {
                    ActionButtonSection(
                        onRequestBloodClick = onRequestBloodClick,
                        onDonateNowClick = onDonateNowClick
                    )
                }
                item { SectionTitle("URGENT APPEALS", "Urgent Appeals Near You") }
                item {
                    BloodTypeFilterRow(
                        selected = state.selectedBloodTypeFilter,
                        onSelected = { viewModel.onBloodTypeFilterChanged(it) }
                    )
                }
                item {
                    UrgentAppealsList(
                        requests = state.filteredRequests,
                        onViewRequest = { request ->
                            // ✅ بمجرد الضغط، بنسجل الطلب المختار والشيت هيفتح فوراً بشكل سليم
                            selectedRequest = request
                            onViewRequest(request)
                        }
                    )
                }
            }
        }
    }

    // ✅ استدعاء الشيت بنفس أسلوب صفحة الأوردرات بالملي خارج الـ PullToRefresh
    if (selectedRequest != null) {
        RequestDetailsBottomSheet(
            request = selectedRequest!!,
            onDismiss = { selectedRequest = null } // يصفر الطلب فيقفل الشيت وينضف الـ Backdrop تماماً
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPrev() {
    HomeScreen()
}