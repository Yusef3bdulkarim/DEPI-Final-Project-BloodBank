package com.example.bloodlink.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
fun RegisterScreen(navController: NavController) {

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var selectedBlood by remember { mutableStateOf("") }
    val bloodTypes = listOf("A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-")

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
                text = "إنشاء حساب جديد",
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                color = PrimaryRed,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            BloodLinkTextField(
                value = name,
                onValueChange = { name = it },
                label = "الاسم",
                leadingIcon = Icons.Default.Person
            )

            Spacer(modifier = Modifier.height(12.dp))

            BloodLinkTextField(
                value = email,
                onValueChange = { email = it },
                label = "البريد الإلكتروني",
                leadingIcon = Icons.Default.Email
            )

            Spacer(modifier = Modifier.height(12.dp))

            BloodLinkTextField(
                value = phone,
                onValueChange = { phone = it },
                label = "رقم الهاتف",
                leadingIcon = Icons.Default.Phone
            )

            Spacer(modifier = Modifier.height(12.dp))

            BloodLinkTextField(
                value = password,
                onValueChange = { password = it },
                label = "كلمة المرور",
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
                text = "إنشاء حساب",
                onClick = {}
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text("عندك حساب؟ ", color = TextDark)
                Text(
                    text = "سجل دخول",
                    color = PrimaryRed,
                    modifier = Modifier.clickable { navController.popBackStack() }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
