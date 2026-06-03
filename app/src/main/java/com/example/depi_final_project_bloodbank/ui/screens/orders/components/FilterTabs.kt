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

@Composable
fun FilterTabs(
    selected: RequestStatus,
    onSelected: (RequestStatus) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listOf(RequestStatus.ACTIVE, RequestStatus.COMPLETED, RequestStatus.CANCELLED,
            RequestStatus.EXPIRED).forEach { status ->
            val isSelected = selected == status
            val bgColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
            val txtColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(CircleShape)
                    .background(bgColor)
                    .clickable { onSelected(status) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(when (status) {
                        RequestStatus.ACTIVE -> R.string.tab_in_progress
                        RequestStatus.COMPLETED -> R.string.tab_completed
                        RequestStatus.EXPIRED -> R.string.tab_expired
                        else -> R.string.tab_cancelled
                    }),
                    color = txtColor,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}
