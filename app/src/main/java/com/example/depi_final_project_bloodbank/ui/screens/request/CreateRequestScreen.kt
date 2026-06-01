package com.example.depi_final_project_bloodbank.ui.screens.request

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.depi_final_project_bloodbank.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRequestScreen(
    viewModel: RequestViewModel,
    onNavigateToDetails: () -> Unit,
    onBackClick: () -> Unit
) {
    val request by viewModel.request.collectAsState()
    val error by viewModel.error.collectAsState()

    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val shapes = MaterialTheme.shapes

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 24.dp, bottom = 16.dp)
        ) {
            IconButton(
                onClick = { onBackClick() } // هنا بننده على أمر الرجوع
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.size(28.dp),
                    tint = colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .background(colorScheme.primary, shapes.small)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text("BL", color = colorScheme.onPrimary, style = typography.labelSmall)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                stringResource(R.string.create_request_title),
                color = colorScheme.primary,
                style = typography.titleLarge
            )

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(28.dp))
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = stringResource(R.string.create_request_title),
                style = typography.titleLarge,
                color = colorScheme.onSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                textAlign = TextAlign.Center
            )

            var expanded by remember { mutableStateOf(false) }
            val bloodTypes = listOf("A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-")

            Card(
                shape = shapes.medium,
                colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
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
                                request.bloodType,
                                style = typography.titleMedium,
                                color = colorScheme.primary
                            )
                        }
                    }
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }) {
                        bloodTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type, style = typography.bodyLarge) },
                                onClick = {
                                    viewModel.updateRequest(request.copy(bloodType = type))
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            Card(
                shape = shapes.medium,
                colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        stringResource(R.string.units_required),
                        style = typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )

                    Box(
                        modifier = Modifier
                            .clickable {
                                if (request.units > 1) viewModel.updateRequest(
                                    request.copy(units = request.units - 1)
                                )
                            }
                            .background(Color.LightGray, shapes.small)
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text("-", style = typography.titleLarge, color = Color.White)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(request.units.toString(), style = typography.titleLarge)
                    Spacer(modifier = Modifier.width(16.dp))
                    Box(
                        modifier = Modifier
                            .clickable { viewModel.updateRequest(request.copy(units = request.units + 1)) }
                            .background(colorScheme.primary, shapes.small)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("+", style = typography.titleLarge, color = colorScheme.onPrimary)
                    }
                }
            }

            Text(
                stringResource(R.string.location_details),
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
                    OutlinedTextField(
                        value = request.hospitalName,
                        onValueChange = { viewModel.updateRequest(request.copy(hospitalName = it)) },
                        placeholder = {
                            Text(
                                stringResource(R.string.hospital_name),
                                style = typography.bodyLarge,
                                color = Color.Gray
                            )
                        },
                        isError = error == "REQUIRED" && request.hospitalName.isBlank(),
                        shape = shapes.medium,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color.LightGray)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = request.city,
                        onValueChange = { viewModel.updateRequest(request.copy(city = it)) },
                        placeholder = {
                            Text(
                                stringResource(R.string.city_area),
                                style = typography.bodyLarge,
                                color = Color.Gray
                            )
                        },
                        isError = error == "REQUIRED" && request.city.isBlank(),
                        shape = shapes.medium,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color.LightGray)
                    )
                }
            }

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
                            selected = request.urgency == "Routine",
                            onClick = { viewModel.updateRequest(request.copy(urgency = "Routine")) },
                            label = { Text(stringResource(R.string.routine_urgency)) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = colorScheme.tertiary,
                                selectedLabelColor = colorScheme.onTertiary
                            ),
                            shape = shapes.large
                        )
                        FilterChip(
                            selected = request.urgency == "Urgent",
                            onClick = { viewModel.updateRequest(request.copy(urgency = "Urgent")) },
                            label = { Text(stringResource(R.string.urgent_urgency)) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = colorScheme.primary,
                                selectedLabelColor = colorScheme.onPrimary
                            ),
                            shape = shapes.large
                        )
                        FilterChip(
                            selected = request.urgency == "Very Urgent",
                            onClick = { viewModel.updateRequest(request.copy(urgency = "Very Urgent")) },
                            label = { Text(stringResource(R.string.very_urgent_urgency)) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = colorScheme.error,
                                selectedLabelColor = colorScheme.onError
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

            Text(
                stringResource(R.string.contact_info),
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
                    OutlinedTextField(
                        value = request.contactName,
                        onValueChange = { viewModel.updateRequest(request.copy(contactName = it)) },
                        placeholder = {
                            Text(
                                stringResource(R.string.contact_name),
                                style = typography.bodyLarge,
                                color = Color.Gray
                            )
                        },
                        shape = shapes.medium,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color.LightGray)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = request.contactPhone,
                        onValueChange = { viewModel.updateRequest(request.copy(contactPhone = it)) },
                        placeholder = {
                            Text(
                                stringResource(R.string.contact_phone),
                                style = typography.bodyLarge,
                                color = Color.Gray
                            )
                        },
                        isError = (error == "REQUIRED" && request.contactPhone.isBlank()) || error == "INVALID_PHONE",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        trailingIcon = {
                            Icon(
                                Icons.Default.Call,
                                contentDescription = "Phone",
                                tint = Color.Gray
                            )
                        },
                        shape = shapes.medium, modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color.LightGray)
                    )
                }
            }

            if (error != null) {
                val errorMsg =
                    if (error == "INVALID_PHONE") stringResource(R.string.error_phone_invalid) else stringResource(
                        R.string.error_required
                    )
                Text(
                    text = errorMsg,
                    color = colorScheme.error,
                    style = typography.titleMedium,
                    modifier = Modifier
                        .padding(bottom = 12.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }

            Button(
                onClick = {
                    if (viewModel.publish()) {
                        onNavigateToDetails()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary),
                shape = shapes.large,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    stringResource(R.string.post_request_btn),
                    color = colorScheme.onPrimary,
                    style = typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = { },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    stringResource(R.string.cancel_btn),
                    color = colorScheme.onSurface,
                    style = typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}