package com.example.bloodlink.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.bloodlink.components.BloodLinkOutlinedButton
import com.example.bloodlink.components.LogoHeader
import com.example.bloodlink.ui.theme.PrimaryRed
import com.example.bloodlink.ui.theme.TextDark

@Composable
fun VerifyAccountScreen(navController: NavController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        LogoHeader()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(Color.White)
                .padding(start = 20.dp, end = 20.dp, top = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            Text(
                text = "يجب تأكيد الحساب",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryRed
            )

            Spacer(modifier = Modifier.height(20.dp))

            Image(
                painter = painterResource(id = R.drawable.email),
                contentDescription = null,
                modifier = Modifier.size(140.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "يجب عليك تأكيد الحساب من خلال الرابط المرسل إلى بريدك الإلكتروني",
                textAlign = TextAlign.Center,
                color = TextDark
            )

            Spacer(modifier = Modifier.height(24.dp))

            BloodLinkButton(
                text = "إرسال رابط التأكيد مرة أخري",
                onClick = {}
            )

            Spacer(modifier = Modifier.height(12.dp))

            BloodLinkOutlinedButton(
                text = "العودة لتسجيل الدخول",
                onClick = { navController.navigate("login") }
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
