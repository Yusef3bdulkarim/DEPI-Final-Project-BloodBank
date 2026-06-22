package com.example.depi_final_project_bloodbank.ui.screens.request

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.depi_final_project_bloodbank.R
import com.example.depi_final_project_bloodbank.ui.screens.request.components.*
import com.example.depi_final_project_bloodbank.ui.theme.MaroonPrimary
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
    val isLoading by viewModel.isLoading.collectAsState()
    val isSuccess by viewModel.isSuccess.collectAsState()
    val isLoadingLocation by viewModel.locationLoading.collectAsState()
    val isLocationFetched by viewModel.locationSuccess.collectAsState()

    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val shapes = MaterialTheme.shapes
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // Logic Validation States
    val hasAttemptedSubmit = error != null
    val isBloodTypeError = hasAttemptedSubmit && (request.bloodType.isBlank() || request.bloodType == "-")
    val isGovMissing = hasAttemptedSubmit && request.governorate.isBlank()
    val isCityMissing = hasAttemptedSubmit && request.city.isBlank()
    val isLocationMissing = isGovMissing || isCityMissing
    val isHospitalError = hasAttemptedSubmit && request.hospitalName.isBlank()
    val isPhoneRequiredError = hasAttemptedSubmit && request.contactPhone.isBlank()
    val isPhoneInvalidError = error == "INVALID_PHONE"

    LaunchedEffect(isSuccess) { if (isSuccess) onNavigateToDetails() }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true || permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            viewModel.fetchCurrentLocation(context, fusedLocationClient)
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(colorScheme.background)) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 24.dp)) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = colorScheme.onSurface)
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(stringResource(R.string.create_request_title), color = colorScheme.primary, style = typography.titleLarge)
            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(48.dp))
        }

        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp).verticalScroll(rememberScrollState())) {
            BloodTypeCard(selectedBloodType = request.bloodType, isError = isBloodTypeError, onBloodTypeSelected = { viewModel.updateRequest(request.copy(bloodType = it)) })
            UnitsNeededCard(unitsNeeded = request.unitsNeeded, onIncrement = { viewModel.updateRequest(request.copy(unitsNeeded = request.unitsNeeded + 1)) }, onDecrement = { if (request.unitsNeeded > 1) viewModel.updateRequest(request.copy(unitsNeeded = request.unitsNeeded - 1)) })

            LocationDetailsCard(
                hospitalName = request.hospitalName, selectedGovernorate = request.governorate, selectedCity = request.city,
                isLoadingLocation = isLoadingLocation, isLocationFetched = isLocationFetched, isLocationMissing = isLocationMissing,
                isGovMissing = isGovMissing, isCityMissing = isCityMissing, isHospitalError = isHospitalError,
                onHospitalNameChange = { viewModel.updateRequest(request.copy(hospitalName = it)) },
                onGovernorateSelected = { viewModel.updateRequest(request.copy(governorate = it, city = "")) },
                onCitySelected = { viewModel.updateRequest(request.copy(city = it)) },
                onFetchLocationClick = { locationPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)) }
            )

            UrgencyStatusCard(priority = request.priority, onPrioritySelected = { viewModel.updateRequest(request.copy(priority = it)) })
            ContactInfoCard(contactName = request.contactName, contactPhone = request.contactPhone, isPhoneRequiredError = isPhoneRequiredError, isPhoneInvalidError = isPhoneInvalidError, onNameChange = { viewModel.updateRequest(request.copy(contactName = it)) }, onPhoneChange = { viewModel.updateRequest(request.copy(contactPhone = it)) })

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.publish() },
                // شرط النشر: لا يوجد تحميل + فصيلة الدم مختارة + المدينة والمحافظة مختارين + الاسم مكتوب + التليفون 11 رقم
                enabled = !isLoading &&
                        request.bloodType.isNotBlank() && request.bloodType != "-" &&
                        request.governorate.isNotBlank() &&
                        request.city.isNotBlank() &&
                        request.contactPhone.length >= 11 &&
                        request.contactName.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = MaroonPrimary),
                shape = shapes.large,
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                if (isLoading) CircularProgressIndicator(color = colorScheme.onPrimary, modifier = Modifier.size(28.dp)) else Text(stringResource(R.string.post_request_btn), color = colorScheme.onPrimary)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}