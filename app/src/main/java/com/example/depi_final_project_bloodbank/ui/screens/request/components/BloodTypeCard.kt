package com.example.depi_final_project_bloodbank.ui.screens.request.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.depi_final_project_bloodbank.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BloodTypeCard(
    selectedBloodType: String,
    isError: Boolean,
    onBloodTypeSelected: (String) -> Unit
) {
    var expandedBloodType by remember { mutableStateOf(false) }
    val bloodTypes = listOf("A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-")
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val shapes = MaterialTheme.shapes

    Column {
        Card(
            shape = shapes.medium,
            colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
                .then(
                    if (isError) Modifier.border(1.5.dp, colorScheme.error, shapes.medium)
                    else Modifier
                )
        ) {
            ExposedDropdownMenuBox(
                expanded = expandedBloodType,
                onExpandedChange = { expandedBloodType = !expandedBloodType }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .menuAnchor()
                ) {
                    Text(
                        stringResource(R.string.blood_type),
                        style = typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Box(
                        modifier = Modifier
                            .border(1.dp, Color.LightGray, shapes.small)
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            selectedBloodType.ifBlank { "-" },
                            style = typography.titleMedium,
                            color = colorScheme.primary
                        )
                    }
                }
                ExposedDropdownMenu(
                    expanded = expandedBloodType,
                    onDismissRequest = { expandedBloodType = false },
                    modifier = Modifier.background(Color.White)
                ) {
                    bloodTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type, style = typography.bodyLarge) },
                            onClick = {
                                onBloodTypeSelected(type)
                                expandedBloodType = false
                            }
                        )
                    }
                }
            }
        }

        if (isError) {
            Text(
                text = stringResource(R.string.error_required),
                color = colorScheme.error,
                style = typography.bodySmall,
                modifier = Modifier.padding(start = 8.dp, bottom = 16.dp, top = 4.dp)
            )
        } else {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}