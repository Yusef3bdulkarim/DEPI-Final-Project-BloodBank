package com.example.bloodlink.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bloodlink.R

@Composable
fun LoginScreen(navController: NavController) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val textColor = Color(0xFF1A1A1A)
    val primaryColor = Color(0xFF8B0000)

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = textColor,
        unfocusedTextColor = textColor,
        cursorColor = primaryColor,
        focusedBorderColor = primaryColor,
        unfocusedBorderColor = Color.Gray
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        Image(
            painter = painterResource(id = R.drawable.bg_pattern),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            alpha = 0.8f,
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = (-40).dp)
                .padding( start = 20.dp, end = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(id = R.drawable.bloodlink),
                contentDescription = null,
                modifier = Modifier.size(180.dp),
                contentScale = ContentScale.Fit
            )

            Text(
                text = "تسجيل الدخول",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = primaryColor,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Image(
                painter = painterResource(id = R.drawable.man),
                contentDescription = null,
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(25.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("البريد الإلكتروني") },
                leadingIcon = { Icon(Icons.Default.Email, null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = textFieldColors
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("كلمة المرور") },
                leadingIcon = { Icon(Icons.Default.Lock, null) },
                visualTransformation =
                    if (passwordVisible) VisualTransformation.None
                    else PasswordVisualTransformation(),
                trailingIcon = {
                    Text(
                        text = if (passwordVisible) "إخفاء" else "إظهار",
                        modifier = Modifier.clickable {
                            passwordVisible = !passwordVisible
                        },
                        color = primaryColor
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = textFieldColors
            )

            Text(
                text = "هل نسيت كلمة المرور؟",
                color = textColor,
                fontSize = 14.sp,
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable { }
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(primaryColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("تسجيل الدخول", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("ليس لديك حساب؟ ", color = textColor)
                Text(
                    text = "إنشاء حساب",
                    color = primaryColor,
                    modifier = Modifier.clickable {
                        navController.navigate("register")
                    }
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {

                Image(
                    painter = painterResource(id = R.drawable.google),
                    contentDescription = "Google",
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color.White, CircleShape)
                        .padding(10.dp)
                        .clickable { }
                )

                Spacer(modifier = Modifier.width(16.dp))

                Image(
                    painter = painterResource(id = R.drawable.icloud),
                    contentDescription = "Apple",
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color.White, CircleShape)
                        .padding(10.dp)
                        .clickable { }
                )
            }
        }
    }
}