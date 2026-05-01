package com.example.depi_final_project_bloodbank.ui.screens.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun UrgentAppealsList() {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            UrgentAppealCard(
                hospitalName = "Kafr El-Sheikh Gen. Hosp.",
                distance = "1.2 km",
                unitsNeeded = 2,
                bloodType = "B-",
                onClickView = { /* Handle Click */ }
            )
        }
        item {
            UrgentAppealCard(
                hospitalName = "University Hospital",
                distance = "3.8 km",
                unitsNeeded = 1,
                isUrgent = false,
                bloodType = "A+",
                onClickView = { /* Handle Click */ }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun UrgentAppealsListPrev() {
UrgentAppealsList()
}