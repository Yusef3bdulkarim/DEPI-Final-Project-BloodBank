package com.example.depi_final_project_bloodbank.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun HomeScreen(navController: NavController) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    var isLoading by remember { mutableStateOf(true) }

    // الحارس: التحقق من استكمال البيانات أول ما الشاشة تفتح
    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            FirebaseFirestore.getInstance().collection("Users").document(currentUser.uid).get()
                .addOnSuccessListener { document ->
                    if (!document.exists() || !document.contains("bloodType")) {
                        // لو مفيش بيانات أو فصيلة الدم ناقصة، اطرده على شاشة استكمال البيانات
                        navController.navigate("complete_profile") {
                            popUpTo("home_screen") { inclusive = true }
                        }
                    } else {
                        isLoading = false // البيانات كاملة، اظهر الشاشة
                    }
                }
                .addOnFailureListener {
                    isLoading = false
                }
        } else {
            isLoading = false
        }
    }

    if (isLoading) {
        // شاشة تحميل بسيطة لحد ما يتأكد من البيانات
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "مرحباً بك في BloodLink!", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = {
                FirebaseAuth.getInstance().signOut()
                navController.navigate("login") {
                    popUpTo("home_screen") { inclusive = true }
                }
            }) {
                Text("تسجيل خروج")
            }
        }
    }
}