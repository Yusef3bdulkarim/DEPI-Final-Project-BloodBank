package com.example.depi_final_project_bloodbank.navigation

import BloodLinkBottomNav
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.example.depi_final_project_bloodbank.ui.screens.CompleteProfileScreen
import com.example.depi_final_project_bloodbank.ui.screens.ForgotPasswordScreen
import com.example.depi_final_project_bloodbank.ui.screens.LoginScreen
import com.example.depi_final_project_bloodbank.ui.screens.RegisterScreen
import com.example.depi_final_project_bloodbank.ui.screens.VerifyAccountScreen
import com.google.firebase.auth.FirebaseAuth

// لو الـ Imports دي لونها أحمر، اقف عليها ودوس Alt + Enter عشان يجيب مسار التيم الصح
import com.example.depi_final_project_bloodbank.ui.screens.home.HomeScreen
import com.example.depi_final_project_bloodbank.ui.screens.notification.NotificationScreen
import com.example.depi_final_project_bloodbank.ui.screens.profile.ProfileScreen
import com.example.depi_final_project_bloodbank.ui.screens.request.CreateRequestScreen
import com.example.depi_final_project_bloodbank.ui.screens.request.RequestViewModel

// استورد الـ HomeScreen والـ ProfileScreen بتوع التيم هنا (Alt + Enter)

@Composable
fun AppNav() {
    val navController = rememberNavController()
    val currentUser = FirebaseAuth.getInstance().currentUser

    // 1. غيرنا البداية لـ "home" بدل "home_screen" لو هو مسجل دخول
    val startDest = if (currentUser != null) "home" else "login"

    // 2. بنجيب مسار الشاشة الحالية عشان نعرف إحنا فين
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // 3. قايمة بالشاشات اللي مسموح يظهر فيها الشريط السفلي بتاع التيم
    val bottomBarScreens = listOf("home", "notifications", "centers", "profile")

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
                        // هنا بنقوله لما تدوس على الزرار روح للمسار اللي سجلناه تحت
                        navController.navigate("create_request")
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
                // 1. بنستدعي الـ ViewModel الخاص بشاشة الطلب
                val requestViewModel: RequestViewModel = viewModel()

                // 2. بنستدعي الشاشة ونباصي ليها الأحداث المطلوبة
                CreateRequestScreen(
                    viewModel = requestViewModel,
                    onBackClick = {
                        // لما يدوس سهم الرجوع، نرجعه للشاشة اللي فاتت
                        navController.popBackStack()
                    },
                    onNavigateToDetails = {
                        // لما الطلب يتنشر بنجاح، نرجعه للـ Home
                        // (لو التيم عامل شاشة اسمها "details"، تقدر تغير الكلمة دي ليها بعدين)
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = false }
                        }
                    }
                )
            }

            // تقدر تضيف الـ notifications والـ centers بنفس الطريقة بعدين
        }
    }
}