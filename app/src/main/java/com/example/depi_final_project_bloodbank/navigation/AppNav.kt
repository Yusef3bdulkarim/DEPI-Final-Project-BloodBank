package com.example.depi_final_project_bloodbank.navigation

import BloodLinkBottomNav
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.depi_final_project_bloodbank.ui.screens.auth.CompleteProfileScreen
import com.example.depi_final_project_bloodbank.ui.screens.auth.ForgotPasswordScreen
import com.example.depi_final_project_bloodbank.ui.screens.auth.LoginScreen
import com.example.depi_final_project_bloodbank.ui.screens.auth.RegisterScreen
import com.example.depi_final_project_bloodbank.ui.screens.auth.VerifyAccountScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.depi_final_project_bloodbank.ui.screens.home.HomeScreen
import com.example.depi_final_project_bloodbank.ui.screens.notification.NotificationScreen
import com.example.depi_final_project_bloodbank.ui.screens.profile.ProfileScreen
import com.example.depi_final_project_bloodbank.ui.screens.request.CreateRequestScreen
import com.example.depi_final_project_bloodbank.ui.screens.request.RequestViewModel
import com.example.depi_final_project_bloodbank.data.repository.RequestRepositoryImpl
import com.example.depi_final_project_bloodbank.ui.screens.profile.DonationHistoryScreen

// استيرادات صريحة من حزمة الـ orders
import com.example.depi_final_project_bloodbank.ui.screens.orders.RequestsScreen
import com.example.depi_final_project_bloodbank.ui.screens.orders.RequestsViewModel
import com.example.depi_final_project_bloodbank.ui.screens.orders.ManageRequestScreen

@Composable
fun AppNav() {
    val navController = rememberNavController()

    var startDest by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            if (!currentUser.isEmailVerified) {
                startDest = "verify_account"
            } else {
                FirebaseFirestore.getInstance().collection("Users").document(currentUser.uid).get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            val name = document.getString("name")
                            val phone = document.getString("phone")
                            val bloodType = document.getString("bloodType")
                            val gov = document.getString("governorate")
                            val city = document.getString("city")

                            if (!name.isNullOrBlank() &&
                                !phone.isNullOrBlank() &&
                                !bloodType.isNullOrBlank() &&
                                !gov.isNullOrBlank() &&
                                !city.isNullOrBlank()
                            ) {
                                startDest = "home"
                            } else {
                                startDest = "complete_profile"
                            }
                        } else {
                            startDest = "complete_profile"
                        }
                    }
                    .addOnFailureListener {
                        startDest = "login"
                    }
            }
        } else {
            startDest = "login"
        }
    }

    if (startDest == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val bottomBarScreens = listOf("home", "notifications", "requests", "profile")

    Scaffold(
        bottomBar = {
            if (currentRoute in bottomBarScreens) {
                BloodLinkBottomNav(navController = navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDest!!,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("login") { LoginScreen(navController) }
            composable("register") { RegisterScreen(navController) }
            composable("forgot_password") { ForgotPasswordScreen(navController) }
            composable("verify_account") { VerifyAccountScreen(navController) }
            composable("complete_profile") {
                val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                CompleteProfileScreen(navController = navController, uid = uid)
            }

            composable("home") {
                HomeScreen(
                    onRequestBloodClick = {
                        navController.navigate("CreateRequestScreen")
                    },
                    onDonateNowClick = {
                        navController.navigate("requests")
                    },
                    onNotificationsClick = {
                        navController.navigate("notifications")
                    }
                )
            }

            composable("profile") {
                ProfileScreen(navController = navController)
            }
            composable(route = "donation_history") {
                DonationHistoryScreen(navController = navController)
            }

            composable("notifications") {
                NotificationScreen(navController = navController)
            }

            composable(route = "CreateRequestScreen") {
                val factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        val repository = RequestRepositoryImpl()
                        return RequestViewModel(repository) as T
                    }
                }
                val screenViewModel: RequestViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = factory)

                CreateRequestScreen(
                    viewModel = screenViewModel,
                    onNavigateToDetails = {
                        navController.navigate(route = "notifications") {
                            popUpTo(route = "CreateRequestScreen") { inclusive = true }
                        }
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable("requests") {
                val factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        val repository = RequestRepositoryImpl()
                        return RequestsViewModel(repository) as T
                    }
                }
                val vm: RequestsViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = factory)
                RequestsScreen(navController = navController, vm = vm)
            }

            composable(
                route = "manage_request/{requestId}",
                arguments = listOf(navArgument("requestId") { type = NavType.StringType })
            ) { backStackEntry ->
                val requestId = backStackEntry.arguments?.getString("requestId") ?: ""
                val factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        val repository = RequestRepositoryImpl()
                        return RequestsViewModel(repository) as T
                    }
                }
                val vm: RequestsViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = factory)
                ManageRequestScreen(requestId = requestId, navController = navController, vm = vm)
            }
        }
    }
}
