package com.example.bloodlink.ui.screens

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.viewmodel.compose.viewModel // <-- استيراد الـ viewModel
import androidx.navigation.NavController
import com.example.bloodlink.R
import com.example.bloodlink.components.BloodLinkButton
import com.example.bloodlink.components.BloodLinkTextField
import com.example.bloodlink.components.LogoHeader
import com.example.bloodlink.ui.theme.PrimaryRed
import com.example.bloodlink.ui.theme.TextDark
import com.example.bloodlink.viewmodel.AuthState // <-- استيراد الـ AuthState
import com.example.bloodlink.viewmodel.AuthViewModel // <-- استيراد الـ AuthViewModel


@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = viewModel()
) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(value = false) }

    // <-- مراقبة حالة الـ Auth
    val authState by viewModel.authState.collectAsState()

    val currentLocales = AppCompatDelegate.getApplicationLocales()
    val isArabic = currentLocales.toLanguageTags().contains("ar")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {

        TextButton(
            onClick = {
                // تبديل اللغة
                val newLang = if (isArabic) "en" else "ar"
                val appLocale = LocaleListCompat.forLanguageTags(newLang)
                AppCompatDelegate.setApplicationLocales(appLocale)
            },
            modifier = Modifier
                .align(Alignment.End)
                .padding(top = 30.dp, end = 16.dp, start = 16.dp)
        ) {
            Text(
                text = if (isArabic) "English" else "عربي",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryRed
            )
        }


        LogoHeader()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(Color.White)
                .padding(start = 20.dp, end = 20.dp, top = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(id = R.string.login_title),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryRed,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Image(
                painter = painterResource(id = R.drawable.avatar),
                contentDescription = null,
                modifier = Modifier.size(160.dp).clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(25.dp))

            BloodLinkTextField(
                value = email,
                onValueChange = { email = it },
                label = stringResource(id = R.string.email_label),
                leadingIcon = Icons.Default.Email
            )

            Spacer(modifier = Modifier.height(16.dp))

            BloodLinkTextField(
                value = password,
                onValueChange = { password = it },
                label = stringResource(id = R.string.password_label),
                leadingIcon = Icons.Default.Lock,
                isPassword = true,
                passwordVisible = passwordVisible,
                onTogglePassword = { passwordVisible = !passwordVisible },
            )

            Text(
                text = stringResource(id = R.string.forgot_password_question),
                color = TextDark,
                fontSize = 14.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.End)
                    .clickable { navController.navigate("forgot_password") }
            )

            Spacer(modifier = Modifier.height(20.dp))

            BloodLinkButton(
                text = stringResource(id = R.string.login_button),
                onClick = {
                    // <-- استدعاء دالة الـ Login و تمرير البيانات
                    viewModel.login(email, password)
                }
            )

            // <-- التعامل مع الحالات المختلفة (تحميل، خطأ، نجاح)
            Spacer(modifier = Modifier.height(16.dp))
            when (authState) {
                is AuthState.Loading -> {
                    CircularProgressIndicator(color = PrimaryRed)
                }
                is AuthState.Error -> {
                    val errorState = authState as AuthState.Error
                    val errorMessage = errorState.messageId?.let { stringResource(id = it) } ?: errorState.messageStr ?: ""

                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        textAlign = TextAlign.Center
                    )
                }
                is AuthState.Success -> {
                    LaunchedEffect(Unit) {
                        navController.navigate("home_screen") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                }
                else -> {}
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(id = R.string.no_account), color = TextDark)
                Text(
                    text = stringResource(id = R.string.create_account_link),
                    color = PrimaryRed,
                    modifier = Modifier.clickable {
                        navController.navigate("register")
                        viewModel.resetState() // <-- تصفير الحالة عشان ميظهرش أي أيرور قديم في الشاشة الجديدة
                    }
                )
            }

            //Spacer(modifier = Modifier.height(8.dp))
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray)
                Text(
                    text = stringResource(id = R.string.or_sign_in_with),
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // لوجو جوجل بعد تكبيره
            Image(
                painter = painterResource(id = R.drawable.google),
                contentDescription = "Google Sign In",
                modifier = Modifier
                    .size(56.dp) // كبرنا الحجم لـ 56 عشان يكون واضح ومناسب للضغط
                    .clip(CircleShape)
                    .clickable {
                        // هنضيف هنا منطق تسجيل الدخول بجوجل بعدين
                    }
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}


