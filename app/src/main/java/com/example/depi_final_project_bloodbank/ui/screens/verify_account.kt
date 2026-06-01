package com.example.depi_final_project_bloodbank.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.depi_final_project_bloodbank.R
import com.example.depi_final_project_bloodbank.components.BloodLinkButton
import com.example.depi_final_project_bloodbank.components.BloodLinkOutlinedButton
import com.example.depi_final_project_bloodbank.components.LogoHeader
import com.example.depi_final_project_bloodbank.ui.theme.PrimaryRed
import com.example.depi_final_project_bloodbank.ui.theme.TextDark

import com.example.depi_final_project_bloodbank.viewmodel.AuthState
import com.example.depi_final_project_bloodbank.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun VerifyAccountScreen(
    navController: NavController,
    viewModel: AuthViewModel = viewModel() // <-- استدعاء
) {

    val authState by viewModel.authState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current // عشان نراقب دورة حياة الشاشة

    // 1. التوجيه الصحيح بناءً على مراقبة State مباشرة
    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            navController.navigate("home") {
                // مسح الشاشة الحالية بدلاً من popUpTo(0) اللي بتسبب مشاكل
                popUpTo("verify_account") { inclusive = true }
            }
        }
    }

    // 2. فحص حالة الإيميل فور رجوع المستخدم للتطبيق (أسرع وأدق حل)
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.checkEmailVerificationStatus()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }


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
                text = stringResource(id = R.string.verify_account_title),
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
                text = stringResource(id = R.string.verify_account_desc),
                textAlign = TextAlign.Center,
                color = TextDark
            )

            Spacer(modifier = Modifier.height(24.dp))

            BloodLinkButton(
                text = stringResource(id = R.string.resend_verification_button),
                onClick = { viewModel.resendVerificationEmail() } // <-- استدعاء الدالة
            )

            Spacer(modifier = Modifier.height(12.dp))

            BloodLinkOutlinedButton(
                text = stringResource(id = R.string.back_to_login),
                onClick = {
                    FirebaseAuth.getInstance().signOut() // نسجل خروجه عشان يدخل من جديد
                    navController.navigate("login") { popUpTo(0) }
                }
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun VerifyAccountPreview() {

    val navController = rememberNavController()


    VerifyAccountScreen(navController = navController)
}
