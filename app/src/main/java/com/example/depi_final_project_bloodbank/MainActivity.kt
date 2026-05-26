package com.example.depi_final_project_bloodbank

import BloodLinkBottomNav
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.depi_final_project_bloodbank.ui.screens.home.HomeScreen
import com.example.depi_final_project_bloodbank.ui.screens.notification.NotificationScreen
import com.example.depi_final_project_bloodbank.ui.screens.profile.ProfileScreen
import com.example.depi_final_project_bloodbank.ui.screens.request.CreateRequestScreen
import com.example.depi_final_project_bloodbank.ui.screens.request.RequestDetailsScreen
import com.example.depi_final_project_bloodbank.ui.screens.request.RequestViewModel
import com.example.depi_final_project_bloodbank.ui.theme.DEPIFinalProjectBloodBankTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DEPIFinalProjectBloodBankTheme {
                val navController = rememberNavController()

                // التعديل السحري: تعريف الـ ViewModel مرة واحدة هنا عشان يكون مشترك (Shared)
                val sharedRequestViewModel: RequestViewModel = viewModel()

                Scaffold(
                    bottomBar = {
                        BloodLinkBottomNav(navController = navController)
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Home.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Home.route) {
                            HomeScreen(
                                onRequestBloodClick = {
                                    navController.navigate("create_request")
                                }
                            )
                        }

                        composable(Screen.Notifications.route) {
                            NotificationScreen()
                        }

                        composable(Screen.Centers.route) {
                            PlaceholderScreen("Centers Screen")
                        }

                        composable(Screen.Profile.route) {
                            ProfileScreen()
                        }

                        // شاشة إنشاء الطلب
                        composable(route = "create_request") {
                            CreateRequestScreen(
                                viewModel = sharedRequestViewModel, // باصينا النسخة المشتركة
                                onNavigateToDetails = {
                                    navController.navigate("RequestDetailsScreen")
                                },
                                onBackClick = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        // شاشة تفاصيل الطلب
                        composable(route = "RequestDetailsScreen") {
                            RequestDetailsScreen(
                                viewModel = sharedRequestViewModel, // باصينا نفس النسخة هنا
                                onBackClick = {
                                    navController.popBackStack()
                                },
                                onNavigateToNotifications = {
                                    navController.navigate(Screen.Notifications.route)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun PlaceholderScreen(x0: String) {
        TODO("Not yet implemented")
    }
}