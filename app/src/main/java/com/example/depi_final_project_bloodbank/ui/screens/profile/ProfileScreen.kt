package com.example.depi_final_project_bloodbank.ui.screens.profile

import android.app.Activity
import android.content.Context
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
    val activity = context as Activity
    val sharedPref = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

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
                        label = stringResource(R.string.total_donation),
                        value = uiState.totalDonations.toString(),
                        modifier = Modifier.weight(1f)
                    )

                    StatCard(
                        label = stringResource(R.string.last_donate),
                        // التعديل الثالث: استخدام المتغير اللي بيحمل التاريخ أو كلمة "Not Available"
                        value = displayLastDonationDate,
                        modifier = Modifier.weight(1f)
                    )

                    StatCard(
                        label = stringResource(R.string.blood_type_text),
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
                            text = stringResource(R.string.badges),
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
                            title = stringResource(R.string.donations),
                            icon = R.drawable.recent,
                            onClick = { }
                        )

                        MenuItem(
                            title = stringResource(R.string.settings),
                            icon = R.drawable.settings,
                            onClick = {
                                showLanguageDialog = true
                            }
                        )

                        MenuItem(
                            title = stringResource(R.string.logout),
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
                    Text(stringResource(R.string.logout))
                },

                text = {
                    Text(stringResource(R.string.are_you_sure_you_want_to_logout))
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
                            text =  stringResource(R.string.logout),
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
                        Text(stringResource(R.string.cancel_btn))
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
                    Text(stringResource(R.string.choose_language))
                },

                text = {

                    Column {

                        TextButton(
                            onClick = {

                                showLanguageDialog = false


                                // Arabic Language Logic
                                sharedPref.edit()
                                    .putString("lang", "ar")
                                    .apply()
                                activity.recreate()

                            }
                        ) {
                            Text("العربية")
                        }

                        TextButton(
                            onClick = {

                                showLanguageDialog = false

                                // English Language Logic
                                sharedPref.edit()
                                    .putString("lang", "en")
                                    .apply()

                                activity.recreate()
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