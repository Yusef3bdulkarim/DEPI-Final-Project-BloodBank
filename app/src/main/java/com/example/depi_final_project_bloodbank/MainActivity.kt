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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.depi_final_project_bloodbank.ui.screens.home.HomeScreen
import com.example.depi_final_project_bloodbank.ui.screens.notification.NotificationScreen
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
                        modifier = Modifier.padding(innerPadding) // مهم جداً عشان مفيش حاجة تستخبى تحت البار
                    ) {
                        composable(Screen.Home.route) {
                            HomeScreen() // شاشتك الجميلة اللي في الصورة
                        }
                        composable(Screen.Notifications.route) {
                            NotificationScreen()
                        }
                        composable(Screen.Centers.route) {
                            PlaceholderScreen("Centers Screen")
                        }
                        composable(Screen.Profile.route) {
                            PlaceholderScreen("Profile Screen")
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
