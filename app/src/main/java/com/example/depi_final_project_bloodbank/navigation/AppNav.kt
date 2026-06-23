package com.example.depi_final_project_bloodbank.navigation

import BloodLinkBottomNav
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.depi_final_project_bloodbank.ui.screens.auth.*
import com.example.depi_final_project_bloodbank.ui.screens.home.HomeScreen
import com.example.depi_final_project_bloodbank.ui.screens.notification.NotificationScreen
import com.example.depi_final_project_bloodbank.ui.screens.profile.ProfileScreen
import com.example.depi_final_project_bloodbank.ui.screens.request.CreateRequestScreen
import com.example.depi_final_project_bloodbank.ui.screens.request.RequestViewModel
import com.example.depi_final_project_bloodbank.data.repository.RequestRepositoryImpl
import com.example.depi_final_project_bloodbank.ui.screens.profile.DonationHistoryScreen
import com.example.depi_final_project_bloodbank.ui.screens.orders.RequestsScreen
import com.example.depi_final_project_bloodbank.ui.screens.orders.RequestsViewModel
import com.example.depi_final_project_bloodbank.ui.screens.orders.ManageRequestScreen
import com.example.depi_final_project_bloodbank.ui.screens.splash.SplashScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore // <-- استيراد الفايرستور للتحقق
import kotlinx.coroutines.delay

@Composable
fun AppNav() {
    val navController = rememberNavController()
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
            startDestination = "splash",
            modifier = Modifier.padding(innerPadding)
        ) {


            composable("splash") {
                LaunchedEffect(Unit) {
                    delay(3000)
                    val currentUser = FirebaseAuth.getInstance().currentUser
                    if (currentUser != null) {
                        // 1️⃣ الفحص الأول: هل الإيميل متفعل؟
                        if (!currentUser.isEmailVerified) {
                            navController.navigate("verify_account") {
                                popUpTo("splash") { inclusive = true }
                            }
                        } else {
                            // 2️⃣ الفحص الثاني: الإيميل متفعل، نتحقق من اكتمال البروفايل
                            FirebaseFirestore.getInstance().collection("Users").document(currentUser.uid).get()
                                .addOnSuccessListener { document ->
                                    if (document.exists()) {
                                        val name = document.getString("name")
                                        val phone = document.getString("phone")
                                        val bloodType = document.getString("bloodType")
                                        val gov = document.getString("governorate")
                                        val city = document.getString("city")

                                        // التأكد من أن كل الحقول مليانة بالبيانات
                                        if (!name.isNullOrBlank() &&
                                            !phone.isNullOrBlank() &&
                                            !bloodType.isNullOrBlank() &&
                                            !gov.isNullOrBlank() &&
                                            !city.isNullOrBlank()
                                        ) {
                                            navController.navigate("home") {
                                                popUpTo("splash") { inclusive = true }
                                            }
                                        } else {
                                            navController.navigate("complete_profile") {
                                                popUpTo("splash") { inclusive = true }
                                            }
                                        }
                                    } else {
                                        navController.navigate("complete_profile") {
                                            popUpTo("splash") { inclusive = true }
                                        }
                                    }
                                }
                                .addOnFailureListener {
                                    navController.navigate("login") {
                                        popUpTo("splash") { inclusive = true }
                                    }
                                }
                        }
                    } else {
                        // مش مسجل دخول أصلاً، واديه الـ login
                        navController.navigate("login") {
                            popUpTo("splash") { inclusive = true }
                        }
                    }
                }

                // عرض واجهة الـ SplashScreen الحقيقية للتيم أثناء الفحص في الخلفية
                SplashScreen(navController)
            }

            // ==========================================
            // باقي شاشات الـ Auth والتيم الحالية بدون أي تغيير
            // ==========================================
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
                    onRequestBloodClick = { navController.navigate("CreateRequestScreen") },
                    onDonateNowClick = { navController.navigate("requests") },
                    onNotificationsClick = { navController.navigate("notifications") }
                )
            }

            composable("profile") { ProfileScreen(navController = navController) }
            composable(route = "donation_history") { DonationHistoryScreen(navController = navController) }
            composable("notifications") { NotificationScreen(navController = navController) }

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