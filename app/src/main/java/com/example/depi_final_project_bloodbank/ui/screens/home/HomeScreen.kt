package com.example.depi_final_project_bloodbank.ui.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue // 👈 تأكدنا من إضافة الإمبورت ده صراحة
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue  // 👈 وإمبورت الـ setter للـ selectedRequest
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
import com.example.depi_final_project_bloodbank.ui.screens.home.viewmodel.HomeViewModel
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
    // طالما أضفنا الـ getValue فوق، الـ by هنا هتقرأ الـ HomeUiState مباشرة
    val state by viewModel.uiState.collectAsState()

    // متغيّر واحد للإظهار والإغلاق لمنع الـ Conflict
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
                item { TopLogoBar() }
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
                            selectedRequest = request
                            onViewRequest(request)
                        }
                    )
                }
            }
        }
    }

    // عرض الـ BottomSheet خارج الـ PullToRefresh
    if (selectedRequest != null) {
        RequestDetailsBottomSheet(
            request = selectedRequest!!,
            onDismiss = { selectedRequest = null }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPrev() {
    // باصينا الـ ViewModel هنا عشان الـ Preview يشتغل وميديّاش إيرور أثناء الـ Build
    HomeScreen(viewModel = viewModel())
}