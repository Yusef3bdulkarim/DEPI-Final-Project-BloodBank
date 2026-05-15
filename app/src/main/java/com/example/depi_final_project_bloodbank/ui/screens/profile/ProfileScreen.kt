package com.example.depi_final_project_bloodbank.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.depi_final_project_bloodbank.R
import com.example.depi_final_project_bloodbank.ui.screens.profile.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel()
) {

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(

        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "BloodBank",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Image(
                            painter = painterResource(id = R.drawable.notification),
                            contentDescription = "Notifications",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },

        containerColor = MaterialTheme.colorScheme.background

    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            item {
                ProfileHeader(
                    name = uiState.name,
                    location = uiState.location,
                    bloodType = uiState.bloodType
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatCard(
                        label = "Total Donation",
                        value = uiState.totalDonations.toString(),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = "Last Donate",
                        value = uiState.lastDonationDate,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = "Blood Type",
                        value = uiState.bloodType,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Column(
                    modifier = Modifier.padding(top = 12.dp)
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = "Badges",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        TextButton(onClick = { }) {
                            Text(
                                "View All",
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.badges) { badge ->
                            BadgeItem(title = badge.title, type = badge.type)
                        }
                    }
                }
            }

            item {

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {

                    Column {

                        MenuItem(
                            title = "Donations",
                            icon = R.drawable.recent,
                            onClick = { }
                        )

                        MenuItem(
                            title = "Settings",
                            icon = R.drawable.settings,
                            onClick = { }
                        )

                        MenuItem(
                            title = "Logout",
                            icon = R.drawable.logout,
                            onClick = {
                               //put Firebase logout here
                            },
                            isDestructive = true
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))

                AppointmentCard(
                    daysLeft = uiState.nextAppointmentDays
                )
                Spacer(modifier = Modifier.height(8.dp))

            }
        }
    }
}