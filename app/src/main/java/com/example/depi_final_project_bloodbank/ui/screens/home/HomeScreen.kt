package com.example.depi_final_project_bloodbank.ui.screens.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel // متنساش تعمل Import للـ viewModel
import com.example.depi_final_project_bloodbank.ui.screens.home.components.ActionButtonSection
import com.example.depi_final_project_bloodbank.ui.screens.home.components.DonationCounterBanner
import com.example.depi_final_project_bloodbank.ui.screens.home.components.DynamicHealthTipsSection
import com.example.depi_final_project_bloodbank.ui.screens.home.components.HeaderSection
import com.example.depi_final_project_bloodbank.ui.screens.home.components.SectionTitle
import com.example.depi_final_project_bloodbank.ui.screens.home.components.TopLogoBar
import com.example.depi_final_project_bloodbank.ui.screens.home.components.UrgentAppealsList
import com.example.depi_final_project_bloodbank.ui.screens.home.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    onRequestBloodClick: () -> Unit = {},
    viewModel: HomeViewModel = viewModel() // 1. ضفنا الـ ViewModel هنا
) {
    // 2. بنراقب حالة الشاشة عشان نجيب منها البيانات الحقيقية
    val uiState by viewModel.uiState.collectAsState()

    // مسحنا القيم الثابتة بتاعت userName و bloodType من هنا
    val daysElapsed = 44
    val nextDate = "25/9/2026"
    val lastDate = "12/8/2026"

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {

        item { TopLogoBar() }

        // 3. بصينا القيم الحقيقية من الـ uiState
        item { HeaderSection(uiState.userName, uiState.bloodType) }

        item { DonationCounterBanner(daysElapsed, nextDate, lastDate) }
        item { DynamicHealthTipsSection() }
        item { ActionButtonSection(onRequestBloodClick = onRequestBloodClick) }
        item { SectionTitle("URGENT APPEALS", "Urgent Appeals Near Kafr-ElSheikh") }
        item { UrgentAppealsList() }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPrev() {
    HomeScreen()
}