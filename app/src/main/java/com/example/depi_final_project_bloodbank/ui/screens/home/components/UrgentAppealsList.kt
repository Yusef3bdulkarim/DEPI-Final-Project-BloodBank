package com.example.depi_final_project_bloodbank.ui.screens.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.depi_final_project_bloodbank.domain.enums.RequestPriority
import com.example.depi_final_project_bloodbank.domain.model.BloodRequest

@Composable
fun UrgentAppealsList(
    requests: List<BloodRequest>,
    onViewRequest: (BloodRequest) -> Unit = {}
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        requests.forEach { request ->
            UrgentAppealCard(
                hospitalName = request.hospitalName,
                location = "${request.city}, ${request.governorate}".trim(',', ' '),
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