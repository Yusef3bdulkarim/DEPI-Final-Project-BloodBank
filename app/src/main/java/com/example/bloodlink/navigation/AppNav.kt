package com.example.bloodlink.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import com.example.bloodlink.ui.screens.LoginScreen
import com.example.bloodlink.ui.screens.RegisterScreen
import com.example.bloodlink.ui.screens.ForgotPasswordScreen
import com.example.bloodlink.ui.screens.VerifyAccountScreen





@Composable
fun AppNav() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
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
    }
}

