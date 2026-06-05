package com.example.depi_final_project_bloodbank.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource // استيراد ضروري
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.depi_final_project_bloodbank.R
import com.example.depi_final_project_bloodbank.ui.screens.home.components.TopLogoBar
import com.example.depi_final_project_bloodbank.ui.screens.profile.components.*
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = viewModel()
) {

    val context = LocalContext.current

    val uiState by viewModel.uiState.collectAsState()

    var showLogoutDialog by remember {
        mutableStateOf(false)
    }

    var showLanguageDialog by remember {
        mutableStateOf(false)
    }

    // التعديل الأول: تجهيز نص "Not Available" في حالة عدم وجود تاريخ
    val displayLastDonationDate = uiState.lastDonationDate.ifEmpty {
        stringResource(id = R.string.not_available)
    }

    Scaffold(

        topBar = {
            TopLogoBar()
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
                    // التعديل الثاني: استخدام "Loading..." لو الاسم لسه فاضي
                    name = uiState.name.ifEmpty { stringResource(id = R.string.loading) },
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
                        // التعديل الثالث: استخدام المتغير اللي بيحمل التاريخ أو كلمة "Not Available"
                        value = displayLastDonationDate,
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
                    }

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.badges) { badge ->
                            // التعديل الرابع: جلب نصوص الأوسمة المترجمة (Expert, Life Saver, First Year)
                            BadgeItem(
                                title = stringResource(id = badge.titleRes),
                                type = badge.type
                            )
                        }
                    }
                }
            }

            item {

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(top = 8.dp),

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
                            onClick = {
                                showLanguageDialog = true
                            }
                        )

                        MenuItem(
                            title = "Logout",
                            icon = R.drawable.logout,
                            onClick = {
                                showLogoutDialog = true
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

        // Logout Dialog

        if (showLogoutDialog) {

            AlertDialog(
                onDismissRequest = {
                    showLogoutDialog = false
                },

                title = {
                    Text("Logout")
                },

                text = {
                    Text("Are you sure you want to logout?")
                },

                confirmButton = {
                    TextButton(
                        onClick = {
                            showLogoutDialog = false

                            FirebaseAuth.getInstance().signOut()

                            val gso = com.google.android.gms.auth.api.signin.GoogleSignInOptions.Builder(
                                com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN
                            ).build()

                            val googleSignInClient = com.google.android.gms.auth.api.signin.GoogleSignIn.getClient(context, gso)

                            googleSignInClient.signOut().addOnCompleteListener {
                                navController.navigate("login") {
                                    popUpTo(0)
                                }
                            }
                        }
                    ) {
                        Text(
                            text = "Logout",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },

                dismissButton = {

                    TextButton(
                        onClick = {
                            showLogoutDialog = false
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Language Dialog

        if (showLanguageDialog) {

            AlertDialog(
                onDismissRequest = {
                    showLanguageDialog = false
                },

                title = {
                    Text("Choose Language")
                },

                text = {

                    Column {

                        TextButton(
                            onClick = {

                                showLanguageDialog = false

                                // Arabic Language Logic
                            }
                        ) {
                            Text("العربية")
                        }

                        TextButton(
                            onClick = {

                                showLanguageDialog = false

                                // English Language Logic
                            }
                        ) {
                            Text("English")
                        }
                    }
                },

                confirmButton = {}
            )
        }
    }
}