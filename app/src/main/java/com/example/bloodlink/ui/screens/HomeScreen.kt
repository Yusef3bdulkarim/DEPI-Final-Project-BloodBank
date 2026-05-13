package com.example.bloodlink.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "مرحباً بك في BloodLink!", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {
            // تسجيل الخروج من فايربيز
            FirebaseAuth.getInstance().signOut()
            // الرجوع لشاشة تسجيل الدخول ومسح الشاشة الرئيسية من الذاكرة
            navController.navigate("login") {
                popUpTo("home_screen") { inclusive = true }
            }
        }) {
            Text("تسجيل خروج")
        }
    }
}

@Preview(showSystemUi = true, showBackground = false)
@Composable
private fun HomePrev() {
    val navController = rememberNavController()
    HomeScreen(navController)
}
