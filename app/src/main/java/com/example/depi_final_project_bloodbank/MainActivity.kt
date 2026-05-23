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
            // داخل الـ setContent
            DEPIFinalProjectBloodBankTheme {
                val navController = rememberNavController() // تعريف الكنترولر

                Scaffold(
                    bottomBar = {
                        // نداء الـ Nav Bar بتاعك هنا عشان يظهر تحت في كل الشاشات
                        BloodLinkBottomNav(navController = navController)
                    }
                ) { innerPadding ->
                    // الـ NavHost هو اللي بيبدل المحتوى اللي في الصورة فوق الـ Bar
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Home.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        // 1. شاشة الهوم (ضفنا أمر الانتقال)
                        composable(Screen.Home.route) {
                            HomeScreen(
                                onRequestBloodClick = {
                                    // أول ما الزرار يتداس، هنروح للمسار ده
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

                        // 2. شاشتك الجديدة العظمة (عرفناها للـ NavHost)
                        // 2. شاشتك الجديدة العظمة (عرفناها للـ NavHost)
                        // شاشة إنشاء الطلب
                        composable(route = "create_request") {
                            val requestViewModel: RequestViewModel = viewModel()

                            CreateRequestScreen(
                                viewModel = requestViewModel,
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
                            val requestViewModel: RequestViewModel = viewModel()

                            RequestDetailsScreen(
                                viewModel = requestViewModel,
                                onBackClick = {
                                    navController.popBackStack()
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
