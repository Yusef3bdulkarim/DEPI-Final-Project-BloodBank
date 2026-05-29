package com.example.depi_final_project_bloodbank.ui.screens.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun UrgentAppealCard(
    hospitalName: String,
    distance: String,
    unitsNeeded: Int,
    bloodType: String,
    isUrgent: Boolean = true,
    onClickView: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(250.dp).height(145.dp)
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.small
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = if (isUrgent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                    shape = CircleShape,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = if (isUrgent) Color.White else Color.Yellow,
                        modifier = Modifier.padding(4.dp)
                    )
                }
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = if (isUrgent) "URGENT: " else "",
                    style = MaterialTheme.typography.titleMedium,
                    color =  if (isUrgent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                )

                Text(
                    text = bloodType,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color =  if (isUrgent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,

                    )

            }

            Spacer(modifier = Modifier.height(5.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // معلومات المستشفى والمسافة
                Column {
                    Text(
                        hospitalName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary,
                        maxLines = 1
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn, // استبدلها بأيقونة Map pin
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "$distance away",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray,
                            maxLines = 1
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Needs $unitsNeeded units",
                            style = MaterialTheme.typography.labelSmall,
                            color =MaterialTheme.colorScheme.onSurface,
                        )

                        TextButton(onClick = onClickView, modifier = Modifier.padding(0.dp)) {
                            Text("View",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun UrgentAppealCardPrev() {
    UrgentAppealCard("Kafr-Elshiekh Blood-Bank", "", 1, "A+") {}
}