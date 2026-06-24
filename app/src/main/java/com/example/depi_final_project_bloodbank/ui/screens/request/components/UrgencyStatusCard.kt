package com.example.depi_final_project_bloodbank.ui.screens.request.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.depi_final_project_bloodbank.R
import com.example.depi_final_project_bloodbank.domain.enums.RequestPriority

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UrgencyStatusCard(
    priority: RequestPriority,
    onPrioritySelected: (RequestPriority) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val shapes = MaterialTheme.shapes

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            stringResource(R.string.urgency_status),
            style = typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Card(
            shape = shapes.medium,
            colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    FilterChip(
                        selected = priority == RequestPriority.NORMAL,
                        onClick = { onPrioritySelected(RequestPriority.NORMAL) },
                        label = { Text(stringResource(R.string.routine_urgency), style = typography.labelSmall) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = colorScheme.tertiary,
                            selectedLabelColor = colorScheme.onTertiary
                        ),
                        shape = shapes.large
                    )
                    FilterChip(
                        selected = priority == RequestPriority.URGENT,
                        onClick = { onPrioritySelected(RequestPriority.URGENT) },
                        label = { Text(stringResource(R.string.urgent_urgency),style = typography.labelSmall) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = colorScheme.primary,
                            selectedLabelColor = colorScheme.onPrimary
                        ),
                        shape = shapes.large
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    stringResource(R.string.note),
                    style = typography.titleMedium,
                    color = Color.Gray
                )
                Text(
                    stringResource(R.string.urgency_instruction),
                    style = typography.bodyLarge,
                    color = Color.Gray
                )
            }
        }
    }
}