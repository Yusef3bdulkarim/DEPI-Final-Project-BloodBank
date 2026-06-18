package com.example.depi_final_project_bloodbank.ui.screens.request

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.depi_final_project_bloodbank.R
import com.example.depi_final_project_bloodbank.domain.enums.RequestPriority
import com.example.depi_final_project_bloodbank.ui.common_components.GovernorateCitySelector
import com.google.android.gms.location.LocationServices

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRequestScreen(
    viewModel: RequestViewModel,
    onNavigateToDetails: () -> Unit,
    onBackClick: () -> Unit
) {
    val request by viewModel.request.collectAsState()
    val error by viewModel.error.collectAsState()

    // حالات الرفع للفايربيز
    val isLoading by viewModel.isLoading.collectAsState()
    val isSuccess by viewModel.isSuccess.collectAsState()

    // حالات اللوكيشن
    val isLoadingLocation by viewModel.locationLoading.collectAsState()
    val isLocationFetched by viewModel.locationSuccess.collectAsState()

    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val shapes = MaterialTheme.shapes

    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // فحص دقيق لكل حقل لوحده
    val hasAttemptedSubmit = error != null
    val isBloodTypeError = hasAttemptedSubmit && (request.bloodType.isBlank() || request.bloodType == "-")
    val isGovMissing = hasAttemptedSubmit && request.governorate.isBlank()
    val isCityMissing = hasAttemptedSubmit && request.city.isBlank()
    val isLocationMissing = isGovMissing || isCityMissing
    val isHospitalError = hasAttemptedSubmit && request.hospitalName.isBlank()
    val isPhoneRequiredError = hasAttemptedSubmit && request.contactPhone.isBlank()
    val isPhoneInvalidError = error == "INVALID_PHONE"

    // Navigation Effect
    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            onNavigateToDetails()
        }
    }

    // Permission Launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (isGranted) {
            viewModel.fetchCurrentLocation(context,fusedLocationClient)
        }
    }

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
            IconButton(onClick = { onBackClick() }) {
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


            var expandedBloodType by remember { mutableStateOf(false) }
            val bloodTypes = listOf("A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-")

            // كارت فصيلة الدم
            Card(
                shape = shapes.medium,
                colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
                    .then(
                        if (isBloodTypeError) Modifier.border(1.5.dp, colorScheme.error, shapes.medium)
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
                                request.bloodType.ifBlank { "-" },
                                style = typography.titleMedium,
                                color = colorScheme.primary
                            )
                        }
                    }
                    ExposedDropdownMenu(
                        expanded = expandedBloodType,
                        onDismissRequest = { expandedBloodType = false }) {
                        bloodTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type, style = typography.bodyLarge) },
                                onClick = {
                                    viewModel.updateRequest(request.copy(bloodType = type))
                                    expandedBloodType = false
                                }
                            )
                        }
                    }
                }
            }

            if (isBloodTypeError) {
                Text(
                    text = stringResource(R.string.error_required),
                    color = colorScheme.error,
                    style = typography.bodySmall,
                    modifier = Modifier.padding(start = 8.dp, bottom = 16.dp, top = 4.dp)
                )
            } else {
                Spacer(modifier = Modifier.height(16.dp))
            }

            Card(
                shape = shapes.medium,
                colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                ) {
                    Text(
                        stringResource(R.string.units_required),
                        style = typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )

                    Box(
                        modifier = Modifier
                            .clickable {
                                if (request.unitsNeeded > 1) viewModel.updateRequest(
                                    request.copy(unitsNeeded = request.unitsNeeded - 1)
                                )
                            }
                            .background(Color.LightGray, shapes.small)
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text("-", style = typography.titleLarge, color = Color.White)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(request.unitsNeeded.toString(), style = typography.titleLarge)
                    Spacer(modifier = Modifier.width(16.dp))
                    Box(
                        modifier = Modifier
                            .clickable { viewModel.updateRequest(request.copy(unitsNeeded = request.unitsNeeded + 1)) }
                            .background(colorScheme.primary, shapes.small)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("+", style = typography.titleLarge, color = colorScheme.onPrimary)
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.location_details),
                    style = typography.titleMedium
                )
            }

            // زرار الـ GPS
            OutlinedButton(
                onClick = {
                    locationPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                border = if (isLocationMissing) BorderStroke(1.5.dp, colorScheme.error) else ButtonDefaults.outlinedButtonBorder,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = colorScheme.primary),
                shape = shapes.medium
            ) {
                if (isLoadingLocation) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.fetching_location))
                } else if (isLocationFetched) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF4CAF50))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.location_fetched_success), color = Color(0xFF4CAF50))
                } else {
                    Text(stringResource(R.string.fetch_gps_location))
                }
            }

            Card(
                shape = shapes.medium,
                colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    // استدعاء الكومبوننت وتمرير حالات الإيرور
                    GovernorateCitySelector(
                        selectedGovernorate = request.governorate,
                        selectedCity = request.city,
                        onGovernorateSelected = { gov ->
                            viewModel.updateRequest(request.copy(governorate = gov, city = ""))
                        },
                        onCitySelected = { city ->
                            viewModel.updateRequest(request.copy(city = city))
                        },
                        isGovError = isGovMissing,     // تمرير خطأ المحافظة
                        isCityError = isCityMissing    // تمرير خطأ المدينة
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = request.hospitalName,
                        onValueChange = { viewModel.updateRequest(request.copy(hospitalName = it)) },
                        placeholder = {
                            Text(stringResource(R.string.hospital_name), style = typography.bodyLarge, color = Color.Gray)
                        },
                        isError = isHospitalError,
                        shape = shapes.medium,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color.LightGray)
                    )

                    if (isHospitalError) {
                        Text(
                            text = stringResource(R.string.error_required),
                            color = colorScheme.error,
                            style = typography.bodySmall,
                            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                        )
                    }
                }
            }

            if (isLocationMissing) {
                Text(
                    text = stringResource(R.string.error_required),
                    color = colorScheme.error,
                    style = typography.bodySmall,
                    modifier = Modifier.padding(start = 8.dp, bottom = 16.dp)
                )
            } else {
                Spacer(modifier = Modifier.height(12.dp))
            }

            Text(
                stringResource(R.string.urgency_status),
                style = typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Card(
                shape = shapes.medium,
                colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        FilterChip(
                            selected = request.priority == RequestPriority.NORMAL,
                            onClick = { viewModel.updateRequest(request.copy(priority = RequestPriority.NORMAL)) },
                            label = { Text(stringResource(R.string.routine_urgency)) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = colorScheme.tertiary,
                                selectedLabelColor = colorScheme.onTertiary
                            ),
                            shape = shapes.large
                        )
                        FilterChip(
                            selected = request.priority == RequestPriority.URGENT,
                            onClick = { viewModel.updateRequest(request.copy(priority = RequestPriority.URGENT)) },
                            label = { Text(stringResource(R.string.urgent_urgency)) },
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

            Text(
                stringResource(R.string.contact_info),
                style = typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Card(
                shape = shapes.medium,
                colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = request.contactName,
                        onValueChange = { viewModel.updateRequest(request.copy(contactName = it)) },
                        placeholder = {
                            Text(
                                stringResource(R.string.contact_name) + " (اختياري)",
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
                        isError = isPhoneRequiredError || isPhoneInvalidError,
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

                    if (isPhoneRequiredError || isPhoneInvalidError) {
                        val phoneErrorMsg = if (isPhoneInvalidError) stringResource(R.string.error_phone_invalid) else stringResource(R.string.error_required)
                        Text(
                            text = phoneErrorMsg,
                            color = colorScheme.error,
                            style = typography.bodySmall,
                            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { viewModel.publish() },
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary),
                shape = shapes.large,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = colorScheme.onPrimary,
                        modifier = Modifier.size(28.dp),
                        strokeWidth = 3.dp
                    )
                } else {
                    Text(
                        stringResource(R.string.post_request_btn),
                        color = colorScheme.onPrimary,
                        style = typography.titleMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = { onBackClick() },
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