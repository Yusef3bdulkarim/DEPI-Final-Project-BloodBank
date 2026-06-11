package com.example.depi_final_project_bloodbank.ui.common_components


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.depi_final_project_bloodbank.R
import com.example.depi_final_project_bloodbank.domain.enums.EgyptLocations

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GovernorateCitySelector(
    selectedGovernorate: String,
    selectedCity: String,
    onGovernorateSelected: (String) -> Unit,
    onCitySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandedGov by remember { mutableStateOf(false) }
    var expandedCity by remember { mutableStateOf(false) }
    val availableCities = if (selectedGovernorate.isNotEmpty()) EgyptLocations.governoratesMap[selectedGovernorate] ?: emptyList() else emptyList()
    val shapes = MaterialTheme.shapes

    Column(modifier = modifier) {
        // دروب داون المحافظة
        ExposedDropdownMenuBox(
            expanded = expandedGov,
            onExpandedChange = { expandedGov = !expandedGov }
        ) {
            OutlinedTextField(
                value = selectedGovernorate,
                onValueChange = {},
                readOnly = true,
                placeholder = { Text(stringResource(R.string.select_governorate), color = Color.Gray) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGov) },
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                shape = shapes.medium,
                colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color.LightGray)
            )
            ExposedDropdownMenu(
                expanded = expandedGov,
                onDismissRequest = { expandedGov = false }
            ) {
                EgyptLocations.allGovernorates.forEach { gov ->
                    DropdownMenuItem(
                        text = { Text(gov) },
                        onClick = {
                            onGovernorateSelected(gov)
                            expandedGov = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // دروب داون المدينة
        ExposedDropdownMenuBox(
            expanded = expandedCity,
            onExpandedChange = { if (selectedGovernorate.isNotEmpty()) expandedCity = !expandedCity }
        ) {
            OutlinedTextField(
                value = selectedCity,
                onValueChange = {},
                readOnly = true,
                placeholder = { Text(stringResource(R.string.select_city), color = Color.Gray) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCity) },
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                shape = shapes.medium,
                colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color.LightGray),
                enabled = selectedGovernorate.isNotEmpty()
            )
            ExposedDropdownMenu(
                expanded = expandedCity,
                onDismissRequest = { expandedCity = false }
            ) {
                availableCities.forEach { city ->
                    DropdownMenuItem(
                        text = { Text(city) },
                        onClick = {
                            onCitySelected(city)
                            expandedCity = false
                        }
                    )
                }
            }
        }
    }
}