package com.example.depi_final_project_bloodbank.ui.screens.request.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.depi_final_project_bloodbank.R

@Composable
fun ContactInfoCard(
    contactName: String,
    contactPhone: String,
    isPhoneRequiredError: Boolean,
    isPhoneInvalidError: Boolean,
    onNameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val shapes = MaterialTheme.shapes

    Column(modifier = Modifier.fillMaxWidth()) {
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
                    value = contactName,
                    onValueChange = onNameChange,
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
                    value = contactPhone,
                    onValueChange = onPhoneChange,
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
                    shape = shapes.medium,
                    modifier = Modifier.fillMaxWidth(),
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
    }
}