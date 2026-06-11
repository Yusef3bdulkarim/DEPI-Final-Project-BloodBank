package com.example.depi_final_project_bloodbank.ui.screens.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.depi_final_project_bloodbank.domain.enums.RequestPriority
import com.example.depi_final_project_bloodbank.domain.model.BloodRequest

@Composable
fun UrgentAppealsList(
    requests: List<BloodRequest>,
    onViewRequest: (BloodRequest) -> Unit = {}
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(requests) { request ->
            UrgentAppealCard(
                hospitalName = request.hospitalName,
                location = "${request.city}, ${request.governorate}".trim(',',' '),
                units = (request.unitsNeeded - request.unitsConfirmed).coerceAtLeast(1),
                bloodType = request.bloodType,
                isUrgent = request.priority == RequestPriority.URGENT,
                onClickView = { onViewRequest(request) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun UrgentAppealsListPrev() {
    UrgentAppealsList(requests = emptyList())
}