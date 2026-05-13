package com.example.bloodlink.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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

import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.bloodlink.viewmodel.AuthState

import com.example.bloodlink.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var selectedBlood by remember { mutableStateOf("") }
    val bloodTypes = listOf("A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-")
    val authState by viewModel.authState.collectAsState()

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
                .padding(start = 20.dp, end = 20.dp, top = 8.dp)
        ) {
            Text(
                text = stringResource(id = R.string.register_title),
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                color = PrimaryRed,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            // التعامل مع الحالات المختلفة (تحميل، خطأ، نجاح)
            Spacer(modifier = Modifier.height(16.dp))
            when (authState) {
                is AuthState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = PrimaryRed
                    )
                }
                is AuthState.Error -> {
                    val errorState = authState as AuthState.Error
                    val errorMessage = errorState.messageId?.let { stringResource(id = it) } ?: errorState.messageStr ?: ""
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                is AuthState.Success -> {
                    LaunchedEffect(Unit) {
                        navController.navigate("home_screen") {
                            // هنا بنمسح الـ register
                            popUpTo("register") { inclusive = true }
                        }
                    }
                }
                else -> {}
            }

            Spacer(modifier = Modifier.height(20.dp))

            BloodLinkTextField(
                value = name,
                onValueChange = { name = it },
                label = stringResource(id = R.string.name_label),
                leadingIcon = Icons.Default.Person
            )

            Spacer(modifier = Modifier.height(12.dp))

            BloodLinkTextField(
                value = email,
                onValueChange = { email = it },
                label = stringResource(id = R.string.email_label),
                leadingIcon = Icons.Default.Email
            )

            Spacer(modifier = Modifier.height(12.dp))

            BloodLinkTextField(
                value = phone,
                onValueChange = { phone = it },
                label = stringResource(id = R.string.phone_label),
                leadingIcon = Icons.Default.Phone
            )

            Spacer(modifier = Modifier.height(12.dp))

            BloodLinkTextField(
                value = password,
                onValueChange = { password = it },
                label = stringResource(id = R.string.password_label),
                leadingIcon = Icons.Default.Lock,
                isPassword = true,
                passwordVisible = passwordVisible,
                onTogglePassword = { passwordVisible = !passwordVisible }
            )

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.drop),
                            contentDescription = null,
                            modifier = Modifier.size(30.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(id = R.string.blood_type_label),
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        bloodTypes.forEach { type ->
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = if (selectedBlood == type)
                                            PrimaryRed else Color.LightGray,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { selectedBlood = type }
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = type,
                                    fontSize = 12.sp,
                                    color = if (selectedBlood == type)
                                        Color.White else Color.Black
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            BloodLinkButton(
                text = stringResource(id = R.string.register_button),
                onClick = {
                    viewModel.register(
                        name = name,
                        email = email,
                        phone = phone,
                        pass = password,
                        bloodType = selectedBlood
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = stringResource(id = R.string.have_account), color = TextDark)
                Text(
                    text = stringResource(id = R.string.login_link),
                    color = PrimaryRed,
                    modifier = Modifier.clickable { navController.popBackStack() }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // سطر "أو سجل الدخول باستخدام" بين خطين
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


            Image(
                painter = painterResource(id = R.drawable.google),
                contentDescription = "Google Sign In",
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .align(Alignment.CenterHorizontally)
                    .clickable {
                        // هنضيف هنا منطق تسجيل الدخول بجوجل بعدين
                    }
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    // ننشئ NavController وهمي فقط للعرض داخل الـ Preview
    val navController = rememberNavController()

    // استدعاء الشاشة الخاصة بك
    RegisterScreen(navController = navController)
}

