package com.example.depi_final_project_bloodbank.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.depi_final_project_bloodbank.components.LogoHeader
import com.example.depi_final_project_bloodbank.ui.screens.auth.viewmodel.AuthViewModel
import com.example.depi_final_project_bloodbank.R
import com.example.depi_final_project_bloodbank.components.BloodLinkButton
import com.example.depi_final_project_bloodbank.components.BloodLinkTextField
import com.example.depi_final_project_bloodbank.ui.theme.PrimaryRed
import com.example.depi_final_project_bloodbank.ui.screens.auth.viewmodel.AuthState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun CompleteProfileScreen(
    navController: NavController,
    uid: String, // الـ UID اللي جاي من جوجل
    viewModel: AuthViewModel = viewModel()
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var selectedBlood by remember { mutableStateOf("") }
    val bloodTypes = listOf("A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-")
    val authState by viewModel.authState.collectAsState()

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color.White)) {
        LogoHeader()

        Column(modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)) {
            Text(
                text = stringResource(id = R.string.complete_profile_title), // "استكمال بيانات الحساب"
                fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryRed,
                modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(30.dp))

            // الاسم (عشان لو حابب يغير الاسم اللي جاي من جوجل)
            BloodLinkTextField(
                value = name, onValueChange = { name = it },
                label = stringResource(id = R.string.name_label),
                leadingIcon = Icons.Default.Person
            )

            Spacer(modifier = Modifier.height(15.dp))

            // رقم الهاتف
            BloodLinkTextField(
                value = phone, onValueChange = { phone = it },
                label = stringResource(id = R.string.phone_label),
                leadingIcon = Icons.Default.Phone
            )

            Spacer(modifier = Modifier.height(20.dp))

            // فصيلة الدم
            Box(modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(12.dp))
                .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                .padding(12.dp)) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(painter = painterResource(id = R.drawable.drop), contentDescription = null, modifier = Modifier.size(25.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = stringResource(id = R.string.blood_type_label), fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        bloodTypes.forEach { type ->
                            Box(modifier = Modifier
                                .background(
                                    if (selectedBlood == type) PrimaryRed else Color.LightGray,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedBlood = type }
                                .padding(horizontal = 10.dp, vertical = 6.dp)) {
                                Text(text = type, fontSize = 12.sp, color = if (selectedBlood == type) Color.White else Color.Black)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            BloodLinkButton(
                text = stringResource(id = R.string.save_and_continue), // "حفظ ومتابعة"
                onClick = { viewModel.completeProfile(uid, name, phone, selectedBlood) }
            )

            // Handling Loading/Error/Success
            if (authState is AuthState.Loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally), color = PrimaryRed)
            }
            if (authState is AuthState.Error) {
                Text(text = (authState as AuthState.Error).messageStr ?: "", color = Color.Red, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            }
            if (authState is AuthState.Success) {
                LaunchedEffect(Unit) {
                    navController.navigate("home_screen") { popUpTo("complete_profile") { inclusive = true } }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, locale = "ar")
@Composable
fun CompleteProfileScreenPreview() {
    // إنشاء NavController وهمي خاص بالـ Preview
    val navController = rememberNavController()

    // استدعاء الشاشة مع تمرير بيانات وهمية
    CompleteProfileScreen(
        navController = navController,
        uid = "dummy_uid_123"
        // مش محتاجين نمرر الـ ViewModel لأنه بياخد قيمة افتراضية
    )
}