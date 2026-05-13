package com.example.bloodlink.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import com.example.bloodlink.ui.screens.LoginScreen
import com.example.bloodlink.ui.screens.RegisterScreen
import com.example.bloodlink.ui.screens.ForgotPasswordScreen
import com.example.bloodlink.ui.screens.VerifyAccountScreen
import com.example.bloodlink.ui.screens.HomeScreen // هننشئ الشاشة دي حالاً
import com.google.firebase.auth.FirebaseAuth // استدعاء فايربيز

@Composable
fun AppNav() {

    val navController = rememberNavController()

    // 1. نسأل فايربيز: هل في مستخدم مسجل دخول؟
    val currentUser = FirebaseAuth.getInstance().currentUser

    // 2. نحدد الشاشة اللي هيبدأ منها التطبيق
    val startDest = if (currentUser != null) "home_screen" else "login"

    NavHost(
        navController = navController,
        startDestination = startDest
    ) {

        composable("login") {
            LoginScreen(navController)
        }

        composable("register") {
            RegisterScreen(navController)
        }

        composable("forgot_password") {
            ForgotPasswordScreen(navController)
        }

        composable("verify_account") {
            VerifyAccountScreen(navController)
        }

        // الشاشة الرئيسية الجديدة
        composable("home_screen") {
            HomeScreen(navController)
        }
    }
}