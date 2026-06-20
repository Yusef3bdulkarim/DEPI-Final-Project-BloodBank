package com.example.depi_final_project_bloodbank.ui.screens.request.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.depi_final_project_bloodbank.R
import com.example.depi_final_project_bloodbank.ui.common_components.GovernorateCitySelector

@Composable
fun LocationDetailsCard(
    hospitalName: String,
    selectedGovernorate: String,
    selectedCity: String,
    isLoadingLocation: Boolean,
    isLocationFetched: Boolean,
    isLocationMissing: Boolean,
    isGovMissing: Boolean,
    isCityMissing: Boolean,
    isHospitalError: Boolean,
    onHospitalNameChange: (String) -> Unit,
    onGovernorateSelected: (String) -> Unit,
    onCitySelected: (String) -> Unit,
    onFetchLocationClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val shapes = MaterialTheme.shapes

    Column(modifier = Modifier.fillMaxWidth()) {
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
            onClick = onFetchLocationClick,
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

                // استدعاء المكون المشترك للمحافظة والمدينة
                GovernorateCitySelector(
                    selectedGovernorate = selectedGovernorate,
                    selectedCity = selectedCity,
                    onGovernorateSelected = onGovernorateSelected,
                    onCitySelected = onCitySelected,
                    isGovError = isGovMissing,
                    isCityError = isCityMissing
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = hospitalName,
                    onValueChange = onHospitalNameChange,
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
    }
}