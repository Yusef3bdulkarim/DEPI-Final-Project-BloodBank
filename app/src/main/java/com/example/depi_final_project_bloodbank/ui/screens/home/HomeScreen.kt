package com.example.depi_final_project_bloodbank.ui.screens.home

import BloodLinkBottomNav
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.depi_final_project_bloodbank.ui.screens.home.components.ActionButtonSection
import com.example.depi_final_project_bloodbank.ui.screens.home.components.DonationCounterBanner
import com.example.depi_final_project_bloodbank.ui.screens.home.components.DynamicHealthTipsSection
import com.example.depi_final_project_bloodbank.ui.screens.home.components.HeaderSection
import com.example.depi_final_project_bloodbank.ui.screens.home.components.SectionTitle
import com.example.depi_final_project_bloodbank.ui.screens.home.components.TopLogoBar
import com.example.depi_final_project_bloodbank.ui.screens.home.components.UrgentAppealsList

@Composable
fun HomeScreen() {
    val userName = "Ahmed"
    val bloodType = "O+"
    val daysElapsed = 44
    val nextDate = "25/9/2026"
    val lastDate = "12/8/2026"

    Scaffold(
        topBar = { TopLogoBar() },
        containerColor = Color.White,
        bottomBar = { BloodLinkBottomNav() }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                HeaderSection(userName, bloodType)
            }
            item {
                DonationCounterBanner(
                    daysElapsed = daysElapsed, nextDate = nextDate, lastDate = lastDate
                )
            }
            item { DynamicHealthTipsSection() }

            item {
                ActionButtonSection()
            }

            item {
                SectionTitle(
                    "URGENT APPEALS",
                    "Urgent Appeals Near Kafr-ElSheikh"
                )
            }
            item { UrgentAppealsList() }
        }

    }
}


@Preview(showBackground = true)
@Composable
private fun HomeScreenPrev() {
    HomeScreen()
}