package com.example.bloodlink.ui.screens

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
import androidx.navigation.NavController
import com.example.bloodlink.R
import com.example.bloodlink.components.BloodLinkButton
import com.example.bloodlink.components.BloodLinkOutlinedButton
import com.example.bloodlink.components.LogoHeader
import com.example.bloodlink.ui.theme.PrimaryRed
import com.example.bloodlink.ui.theme.TextDark
import com.example.bloodlink.viewmodel.AuthState
import com.example.bloodlink.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun VerifyAccountScreen(
    navController: NavController,
    viewModel: AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel() // <-- استدعاء
) {

    val authState by viewModel.authState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current // عشان نراقب دورة حياة الشاشة

    // 1. التوجيه الصحيح بناءً على مراقبة State مباشرة
    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            navController.navigate("home_screen") {
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

    // 3. الفحص المستمر في الخلفية (Polling) كخطة بديلة لو فضل في الشاشة
    LaunchedEffect(Unit) {
        while (true) {
            delay(10000)
            viewModel.checkEmailVerificationStatus()
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
                    com.google.firebase.auth.FirebaseAuth.getInstance().signOut() // نسجل خروجه عشان يدخل من جديد
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

    val navController = androidx.navigation.compose.rememberNavController()


    VerifyAccountScreen(navController = navController)
}
