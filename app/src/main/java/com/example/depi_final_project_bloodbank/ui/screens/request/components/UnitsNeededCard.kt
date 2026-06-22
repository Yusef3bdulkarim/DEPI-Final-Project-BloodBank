package com.example.depi_final_project_bloodbank.ui.screens.request.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.depi_final_project_bloodbank.R

@Composable
fun UnitsNeededCard(
    unitsNeeded: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val shapes = MaterialTheme.shapes

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
                    .clickable { onDecrement() }
                    .background(Color.LightGray, shapes.small)
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text("-", style = typography.titleLarge, color = Color.White)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(unitsNeeded.toString(), style = typography.titleLarge)

            Spacer(modifier = Modifier.width(16.dp))

            Box(
                modifier = Modifier
                    .clickable { onIncrement() }
                    .background(colorScheme.primary, shapes.small)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text("+", style = typography.titleLarge, color = colorScheme.onPrimary)
            }
        }
    }
}