package com.example.depi_final_project_bloodbank.navigation

import BloodLinkBottomNav
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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

import com.example.depi_final_project_bloodbank.ui.screens.home.HomeScreen
import com.example.depi_final_project_bloodbank.ui.screens.notification.NotificationScreen
import com.example.depi_final_project_bloodbank.ui.screens.orders.RequestsViewModel
import com.example.depi_final_project_bloodbank.ui.screens.profile.ProfileScreen
import com.example.depi_final_project_bloodbank.ui.screens.request.CreateRequestScreen
import com.example.depi_final_project_bloodbank.ui.screens.request.RequestViewModel
import com.example.depi_final_project_bloodbank.data.repository.RequestRepositoryImpl

@Composable
fun AppNav() {
    val navController = rememberNavController()

    val startDest = remember {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) "home" else "login"
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
            startDestination = startDest,
            modifier = Modifier.padding(innerPadding)
        ) {
            // ==========================================
            // القسم الأول: شاشات الـ Auth (بدون شريط سفلي)
            // ==========================================
            composable("login") { LoginScreen(navController) }
            composable("register") { RegisterScreen(navController) }
            composable("forgot_password") { ForgotPasswordScreen(navController) }
            composable("verify_account") { VerifyAccountScreen(navController) }
            composable("complete_profile") {
                val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                CompleteProfileScreen(navController = navController, uid = uid)
            }

            // ==========================================
            // القسم التاني: شاشات التيم (بالشريط السفلي)
            // ==========================================
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
                    },
                    onViewRequest = { request ->
                        navController.navigate("blood_request_details/${request.id}")
                    }
                )
            }

            composable("profile") {
                ProfileScreen(navController = navController)
            }

            composable("notifications") {
                NotificationScreen()
            }

            // ==========================================
            // القسم الثالث: شاشاتك إنت (اللوجيك الجديد)
            // ==========================================
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

                com.example.depi_final_project_bloodbank.ui.screens.orders.RequestsScreen(vm = vm)
            }

            // مسار شاشة التفاصيل بتاع التيم
            composable(
                route = "blood_request_details/{requestId}",
                arguments = listOf(navArgument("requestId") { type = NavType.StringType })
            ) { backStackEntry ->
                val requestId = backStackEntry.arguments?.getString("requestId") ?: ""
                // com.example.depi_final_project_bloodbank.ui.screens.request.UrgentRequestDetailsScreen(...)
            }
        }
    }
}