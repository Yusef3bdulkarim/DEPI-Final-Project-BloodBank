package com.example.bloodlink.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.border
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bloodlink.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController) {

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var selectedBlood by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    val bloodTypes = listOf("A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-")

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

        Image(
            painter = painterResource(id = R.drawable.bloodlink),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 12.dp)
                .size(180.dp),
            contentScale = ContentScale.Fit
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 140.dp, start = 20.dp, end = 20.dp)
        ) {

            Text(
                text = "إنشاء حساب جديد",
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                color = primaryColor,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("الاسم") },
                leadingIcon = { Icon(Icons.Default.Person, null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = textFieldColors
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("البريد الإلكتروني") },
                leadingIcon = { Icon(Icons.Default.Email, null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = textFieldColors
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("رقم الهاتف") },
                leadingIcon = { Icon(Icons.Default.Phone, null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = textFieldColors
            )

            Spacer(modifier = Modifier.height(12.dp))

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
                        text = if (passwordVisible) "إظهار" else "إخفاء",
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

            Spacer(modifier = Modifier.height(12.dp))

            val bloodTypes = listOf("A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-")
            var selectedBlood by remember { mutableStateOf("") }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {

                Column {


                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Image(
                            painter = painterResource(id = R.drawable.drop),
                            contentDescription = null,
                            modifier = Modifier.size(30.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "فصيلة الدم",
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
                                            Color(0xFF8B0000)
                                        else Color.LightGray,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable {
                                        selectedBlood = type
                                    }
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = type,
                                    fontSize = 12.sp,
                                    color = if (selectedBlood == type)
                                        Color.White
                                    else Color.Black
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(primaryColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("إنشاء حساب", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text("عندك حساب؟ ", color = textColor)
                Text(
                    text = "سجل دخول",
                    color = primaryColor,
                    modifier = Modifier.clickable {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}