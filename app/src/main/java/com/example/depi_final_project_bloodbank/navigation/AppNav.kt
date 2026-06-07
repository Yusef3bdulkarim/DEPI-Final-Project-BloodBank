package com.example.depi_final_project_bloodbank.navigation

import BloodLinkBottomNav
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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

// لو الـ Imports دي لونها أحمر، اقف عليها ودوس Alt + Enter عشان يجيب مسار التيم الصح
import com.example.depi_final_project_bloodbank.ui.screens.home.HomeScreen
import com.example.depi_final_project_bloodbank.ui.screens.notification.NotificationScreen
import com.example.depi_final_project_bloodbank.ui.screens.profile.ProfileScreen
import com.example.depi_final_project_bloodbank.ui.screens.request.CreateRequestScreen

// استورد الـ HomeScreen والـ ProfileScreen بتوع التيم هنا (Alt + Enter)

@Composable
fun AppNav() {
    val navController = rememberNavController()
    val sharedRequestViewModel: com.example.depi_final_project_bloodbank.ui.screens.request.RequestViewModel =
        androidx.lifecycle.viewmodel.compose.viewModel()
    val currentUser = FirebaseAuth.getInstance().currentUser

    // 1. غيرنا البداية لـ "home" بدل "home_screen" لو هو مسجل دخول
    val startDest = if (currentUser != null) "home" else "login"

    // 2. بنجيب مسار الشاشة الحالية عشان نعرف إحنا فين
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // 3. قايمة بالشاشات اللي مسموح يظهر فيها الشريط السفلي بتاع التيم
    val bottomBarScreens = listOf("home", "notifications", "requests", "profile")

    Scaffold(
        bottomBar = {
            // لو إحنا في شاشة من الشاشات الأساسية، اعرض الشريط السفلي
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
            // القسم الأول: شاشات الـ Auth بتاعتك (بدون شريط سفلي)
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
                        navController.navigate("create_request")
                    },
                    onDonateNowClick = {
                        navController.navigate("requests")
                    },
                    onNotificationsClick = {
                        navController.navigate("notifications")
                    },
                    // ✅ هنا الربط السحري! مررنا الـ request وضفنا الـ id بتاعه في الـ Route
                    onViewRequest = { request ->
                        navController.navigate("blood_request_details/${request.id}")
                    }
                )
            }

            composable("profile") {
                ProfileScreen(navController = navController)
            }

            // ضفنا مسار الإشعارات هنا عشان التطبيق ميعملش Crash
            composable("notifications") {
                NotificationScreen()
            }
            composable("create_request") {
                CreateRequestScreen(
                    viewModel = sharedRequestViewModel, // استخدام المشترك
                    onNavigateToDetails = {
                        navController.navigate("RequestDetailsScreen")
                    },
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
            // شاشة الطلبات الجديدة
            composable("requests") {
                com.example.depi_final_project_bloodbank.ui.screens.orders.RequestsScreen()
            }
            composable(
                route = "blood_request_details/{requestId}",
                arguments = listOf(navArgument("requestId") { type = NavType.StringType })
            ) { backStackEntry ->
                val requestId = backStackEntry.arguments?.getString("requestId") ?: ""

                // تذكر تستبدل الكومبوزابل ده باسم شاشة التفاصيل الفعلية بتاعتك ممرراً لها الـ requestId
                // com.example.depi_final_project_bloodbank.ui.screens.request.UrgentRequestDetailsScreen(requestId = requestId, navController = navController)
            }
            // شاشة تفاصيل الطلب الجديدة
            /* دي الديتيلز الي بتظهر بعد م الطلب يتعمل انا شيلتها لانها في بتظهر
            بتظهر في بوتوم شيت من صفحة الاوردرات
            composable("RequestDetailsScreen") {
                com.example.depi_final_project_bloodbank.ui.screens.request.RequestDetailsScreen(
                    viewModel = sharedRequestViewModel,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onNavigateToNotifications = {
                        navController.navigate("notifications")
                    }
                )
            }
            */
            // تقدر تضيف الـ notifications والـ centers بنفس الطريقة بعدين
        }
    }
}