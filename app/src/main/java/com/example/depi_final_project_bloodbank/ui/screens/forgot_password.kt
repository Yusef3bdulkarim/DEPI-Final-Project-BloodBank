package com.example.depi_final_project_bloodbank.ui.screens

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.depi_final_project_bloodbank.R
import com.example.depi_final_project_bloodbank.components.BloodLinkButton
import com.example.depi_final_project_bloodbank.components.BloodLinkTextField
import com.example.depi_final_project_bloodbank.components.LogoHeader
import com.example.depi_final_project_bloodbank.ui.theme.PrimaryRed
import com.example.depi_final_project_bloodbank.ui.theme.TextDark
import com.example.depi_final_project_bloodbank.viewmodel.AuthState
import com.example.depi_final_project_bloodbank.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun ForgotPasswordScreen(
    navController: NavController,
    viewModel: AuthViewModel = viewModel() // <-- استدعاء ViewModel
) {

    var email by remember { mutableStateOf("") }
    val authState by viewModel.authState.collectAsState()

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
                text = stringResource(id = R.string.forgot_password_title),
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
                text = stringResource(id = R.string.forgot_password_desc),
                textAlign = TextAlign.Center,
                color = TextDark
            )

            Spacer(modifier = Modifier.height(20.dp))

            BloodLinkTextField(
                value = email,
                onValueChange = { email = it },
                label = stringResource(id = R.string.email_label),
                leadingIcon = Icons.Default.Email
            )

            Spacer(modifier = Modifier.height(20.dp))

            BloodLinkButton(
                text = stringResource(id = R.string.send_reset_link_button),
                onClick = { viewModel.resetPassword(email) }
            )

            Spacer(modifier = Modifier.height(16.dp))
            when (authState) {
                is AuthState.Loading -> CircularProgressIndicator(color = PrimaryRed)
                is AuthState.Error -> Text(text = (authState as AuthState.Error).messageStr ?: "", color = Color.Red, textAlign = TextAlign.Center)
                is AuthState.PasswordResetSent -> {
                    Text(text = stringResource(R.string.resent_email), color = Color(0xFF4CAF50), textAlign = TextAlign.Center)
                    LaunchedEffect(Unit) {
                        delay(3000) // نستنى 3 ثواني وبعدين نرجعه للـ Login
                        navController.popBackStack()
                        viewModel.resetState()
                    }
                }
                else -> {}
            }

            Text(
                text = stringResource(id = R.string.back_to_login),
                modifier = Modifier.clickable { navController.popBackStack() },
                color = TextDark
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}