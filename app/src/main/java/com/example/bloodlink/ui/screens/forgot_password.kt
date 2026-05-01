package com.example.bloodlink.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bloodlink.R
import com.example.bloodlink.components.BloodLinkButton
import com.example.bloodlink.components.BloodLinkTextField
import com.example.bloodlink.components.LogoHeader
import com.example.bloodlink.ui.theme.PrimaryRed
import com.example.bloodlink.ui.theme.TextDark


@Composable
fun ForgotPasswordScreen(navController: NavController) {

    var email by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        LogoHeader()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(Color.White)
                .padding(start = 20.dp, end = 20.dp, top = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "نسيت كلمة المرور؟",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryRed
            )

            Spacer(modifier = Modifier.height(20.dp))

            Image(
                painter = painterResource(id = R.drawable.key),
                contentDescription = null,
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "يرجى إدخال بريدك الإلكتروني أدناه. سنقوم بإرسال رابط لتغيير كلمة المرور.",
                textAlign = TextAlign.Center,
                color = TextDark
            )

            Spacer(modifier = Modifier.height(20.dp))

            BloodLinkTextField(
                value = email,
                onValueChange = { email = it },
                label = "البريد الإلكتروني",
                leadingIcon = Icons.Default.Email
            )

            Spacer(modifier = Modifier.height(20.dp))

            BloodLinkButton(
                text = "إرسال رابط استعادة كلمة المرور",
                onClick = { navController.navigate("verify_account") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "العودة لتسجيل الدخول",
                modifier = Modifier.clickable { navController.popBackStack() },
                color = TextDark
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}