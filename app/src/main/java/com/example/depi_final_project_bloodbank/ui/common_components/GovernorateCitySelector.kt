package com.example.depi_final_project_bloodbank.ui.common_components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.depi_final_project_bloodbank.R
import com.example.depi_final_project_bloodbank.domain.enums.EgyptLocations
import com.example.depi_final_project_bloodbank.ui.theme.PrimaryRed
import com.example.depi_final_project_bloodbank.ui.theme.TextDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GovernorateCitySelector(
    selectedGovernorate: String,
    selectedCity: String,
    onGovernorateSelected: (String) -> Unit,
    onCitySelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    isGovError: Boolean = false,
    isCityError: Boolean = false
) {
    var expandedGov by remember { mutableStateOf(false) }
    var expandedCity by remember { mutableStateOf(false) }
    val availableCities = if (selectedGovernorate.isNotEmpty()) EgyptLocations.governoratesMap[selectedGovernorate] ?: emptyList() else emptyList()
    val shapes = MaterialTheme.shapes

    val colors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = TextDark,
        unfocusedTextColor = TextDark,
        cursorColor = PrimaryRed,
        focusedBorderColor = PrimaryRed,
        unfocusedBorderColor = Color.Gray
    )

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
                placeholder = { Text(stringResource(R.string.select_governorate), color = if (isGovError) MaterialTheme.colorScheme.error else Color.Gray) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGov) },
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                shape = shapes.medium,
                isError = isGovError,
                colors = colors
            )
            ExposedDropdownMenu(
                expanded = expandedGov,
                onDismissRequest = { expandedGov = false },
                modifier = Modifier.background(Color.White)
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
            // القائمة مش هتفتح غير لو المحافظة تم اختيارها
            onExpandedChange = { if (selectedGovernorate.isNotEmpty()) expandedCity = !expandedCity }
        ) {
            OutlinedTextField(
                value = selectedCity,
                onValueChange = {},
                readOnly = true,
                placeholder = { Text(stringResource(R.string.select_city), color = if (isCityError) MaterialTheme.colorScheme.error else Color.Gray) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCity) },
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                shape = shapes.medium,
                isError = isCityError, // دلوقتي هيقبل الإيرور وينور أحمر عادي
                colors = colors
                // تم مسح سطر الـ enabled عشان اللون يشتغل
            )
            ExposedDropdownMenu(
                expanded = expandedCity,
                onDismissRequest = { expandedCity = false },
                modifier = Modifier.background(Color.White)

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