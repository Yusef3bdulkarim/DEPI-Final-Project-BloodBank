package com.example.depi_final_project_bloodbank.ui.screens.orders

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.depi_final_project_bloodbank.R
import com.example.depi_final_project_bloodbank.domain.enums.DonationStatus
import com.example.depi_final_project_bloodbank.utils.toFormattedDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageRequestScreen(
    requestId: String,
    navController: NavController,
    vm: RequestsViewModel
) {
    val state by vm.uiState.collectAsState()
    val request = state.orders.find { it.id == requestId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("إدارة الطلب", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (request == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(text = "ضبط الوحدات المطلوبة", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "الوحدات الحالية", style = MaterialTheme.typography.bodyLarge)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { if (request.unitsNeeded > 1) vm.updateUnitsNeeded(request.id, request.unitsNeeded - 1) }) {
                                Icon(Icons.Default.Remove, contentDescription = null)
                            }
                            Text(text = request.unitsNeeded.toString(), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                            IconButton(onClick = { vm.updateUnitsNeeded(request.id, request.unitsNeeded + 1) }) {
                                Icon(Icons.Default.Add, contentDescription = null)
                            }
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                Text(text = "سجل المتبرعين", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                if (request.donationLog.isEmpty()) {
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        Text(text = "لا توجد عمليات تبرع حالياً", color = MaterialTheme.colorScheme.onSurface)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(request.donationLog.reversed()) { entry ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(1.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = entry.donorName, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                                        Text(
                                            text = "${entry.status} • ${entry.timestamp.toFormattedDate()}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    if (entry.status == DonationStatus.PENDING) {
                                        Button(
                                            onClick = { vm.confirmDonationDelivery(entry.id, request.id) },
                                            shape = MaterialTheme.shapes.small,
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                        ) {
                                            Text("تأكيد الاستلام", style = MaterialTheme.typography.labelSmall)
                                        }
                                    } else {
                                        val statusColor = when (entry.status) {
                                            DonationStatus.CONFIRMED -> MaterialTheme.colorScheme.tertiary
                                            DonationStatus.CANCELLED -> MaterialTheme.colorScheme.error
                                            else -> MaterialTheme.colorScheme.primaryContainer
                                        }
                                        Text(
                                            text = entry.status.name,
                                            color = statusColor,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                if (request.status == com.example.depi_final_project_bloodbank.domain.enums.RequestStatus.ACTIVE) {
                    Button(
                        onClick = {
                            vm.cancelRequest(request.id)
                            navController.popBackStack()
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(stringResource(R.string.btn_cancel_request), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
