package com.example.bloodlink.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import com.example.bloodlink.ui.screens.LoginScreen
import com.example.bloodlink.ui.screens.RegisterScreen

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
    }
}