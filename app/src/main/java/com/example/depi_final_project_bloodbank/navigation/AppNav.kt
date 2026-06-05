package com.example.depi_final_project_bloodbank.navigation

import BloodLinkBottomNav
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import com.example.depi_final_project_bloodbank.ui.screens.auth.CompleteProfileScreen
import com.example.depi_final_project_bloodbank.ui.screens.auth.ForgotPasswordScreen
import com.example.depi_final_project_bloodbank.ui.screens.auth.LoginScreen
import com.example.depi_final_project_bloodbank.ui.screens.auth.RegisterScreen
import com.example.depi_final_project_bloodbank.ui.screens.auth.VerifyAccountScreen
import com.google.firebase.auth.FirebaseAuth

// لو الـ Imports دي لونها أحمر، اقف عليها ودوس Alt + Enter عشان يجيب مسار التيم الصح
import com.example.depi_final_project_bloodbank.ui.screens.home.HomeScreen
import com.example.depi_final_project_bloodbank.ui.screens.notification.NotificationScreen
import com.example.depi_final_project_bloodbank.ui.screens.orders.RequestsViewModel
import com.example.depi_final_project_bloodbank.ui.screens.profile.ProfileScreen
import com.example.depi_final_project_bloodbank.ui.screens.request.CreateRequestScreen
import com.example.depi_final_project_bloodbank.ui.screens.request.RequestViewModel

// استورد الـ HomeScreen والـ ProfileScreen بتوع التيم هنا (Alt + Enter)

@Composable
fun AppNav() {
    val navController = rememberNavController()
    // شيلت الـ sharedRequestViewModel لأنه مش مستخدم في الـ composable
    val currentUser = FirebaseAuth.getInstance().currentUser

    val startDest = if (currentUser != null) "home" else "login"
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
            // --- Auth ---
            composable("login") { LoginScreen(navController) }
            composable("register") { RegisterScreen(navController) }
            composable("forgot_password") { ForgotPasswordScreen(navController) }
            composable("verify_account") { VerifyAccountScreen(navController) }
            composable("complete_profile") {
                val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                CompleteProfileScreen(navController = navController, uid = uid)
            }

            // --- Home ---
            composable("home") {
                HomeScreen(
                    onRequestBloodClick = {
                        navController.navigate("CreateRequestScreen")
                    }
                )
            }

            // --- Profile ---
            composable("profile") {
                ProfileScreen(navController = navController)
            }

            // --- Notifications ---
            composable("notifications") {
                NotificationScreen()
            }

            // --- Create Request (اللوجيك الجديد) ---
            composable(route = "CreateRequestScreen") {
                val factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        val repository = com.example.depi_final_project_bloodbank.data.repository.RequestRepositoryImpl()
                        return RequestViewModel(repository) as T
                    }
                }
                val screenViewModel: RequestViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = factory)

                CreateRequestScreen(
                    viewModel = screenViewModel,
                    onNavigateToDetails = {
                        // هنا التعديل المهم: لازم الـ route يبقى مطابق للي سجلناه فوق في الـ NavHost (سطر 81)
                        navController.navigate(route = "notifications") {
                            popUpTo(route = "CreateRequestScreen") { inclusive = true }
                        }
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }

            // --- Orders ---
            // --- Orders ---
            composable("requests") {
                // 1. عرف الـ Factory
                val factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        val repository = com.example.depi_final_project_bloodbank.data.repository.RequestRepositoryImpl()
                        return RequestsViewModel(repository) as T
                    }
                }
                // 2. عرف الـ ViewModel
                val vm: RequestsViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = factory)

                // 3. ابعت الـ vm للشاشة (ده اللي كان ناقص!)
                com.example.depi_final_project_bloodbank.ui.screens.orders.RequestsScreen(vm = vm)
            }
        }
    }
}