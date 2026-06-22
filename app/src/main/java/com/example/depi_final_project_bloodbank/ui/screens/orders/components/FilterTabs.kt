package com.example.depi_final_project_bloodbank.ui.screens.orders.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.depi_final_project_bloodbank.R
import com.example.depi_final_project_bloodbank.domain.enums.RequestStatus
import com.example.depi_final_project_bloodbank.ui.theme.MaroonPrimary

@Composable
fun FilterTabs(
    selected: RequestStatus,
    onSelected: (RequestStatus) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val statuses = listOf(RequestStatus.ACTIVE, RequestStatus.COMPLETED, RequestStatus.CANCELLED)

        statuses.forEach { status ->
            val isSelected = selected == status
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (isSelected) MaroonPrimary else Color.LightGray.copy(alpha = 0.2f))
                    .clickable { onSelected(status) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = status.name, // أو اسم التاب اللي عايزه
                    color = if (isSelected) Color.White else Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}