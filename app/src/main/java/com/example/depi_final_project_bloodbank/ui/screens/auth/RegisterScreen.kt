package com.example.depi_final_project_bloodbank.ui.screens.auth

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.depi_final_project_bloodbank.R
import com.example.depi_final_project_bloodbank.components.BloodLinkButton
import com.example.depi_final_project_bloodbank.components.BloodLinkTextField
import com.example.depi_final_project_bloodbank.components.LogoHeader
import com.example.depi_final_project_bloodbank.ui.theme.PrimaryRed
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.depi_final_project_bloodbank.ui.common_components.GovernorateCitySelector
import com.example.depi_final_project_bloodbank.ui.theme.TextDark
import com.example.depi_final_project_bloodbank.ui.screens.auth.viewmodel.AuthState
import com.example.depi_final_project_bloodbank.ui.screens.auth.viewmodel.AuthViewModel
import com.example.depi_final_project_bloodbank.ui.screens.home.components.TopLogoBar
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: AuthViewModel = viewModel()
) {

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var selectedBlood by remember { mutableStateOf("") }
    val bloodTypes = listOf("A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-")
    val authState by viewModel.authState.collectAsState()
    var selectedGovernorate by remember { mutableStateOf("") }
    var selectedCity by remember { mutableStateOf("") }
    var lastDonationDate by remember { mutableStateOf<Long?>(null) }
    var lastDonationText by remember { mutableStateOf("") }
    val context = LocalContext.current
    // تجهيز نافذة التقويم (Calendar)
    val calendar = java.util.Calendar.getInstance()
    val datePickerDialog = android.app.DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val selectedCalendar = java.util.Calendar.getInstance()
            selectedCalendar.set(year, month, dayOfMonth)
            lastDonationDate = selectedCalendar.timeInMillis
            lastDonationText = "$dayOfMonth/${month + 1}/$year" // عرض التاريخ للمستخدم
        },
        calendar.get(java.util.Calendar.YEAR),
        calendar.get(java.util.Calendar.MONTH),
        calendar.get(java.util.Calendar.DAY_OF_MONTH)
    )
    // بنمنعه يختار تاريخ في المستقبل (لأنه أكيد متبرعش في المستقبل)
    datePickerDialog.datePicker.maxDate = System.currentTimeMillis()



    // إعدادات جوجل
    val gso = GoogleSignInOptions.Builder(
        GoogleSignInOptions.DEFAULT_SIGN_IN
    )
        .requestIdToken(stringResource(id = R.string.default_web_client_id))
        .requestEmail()
        .build()

    val googleSignInClient = remember { GoogleSignIn.getClient(context, gso) }

    // الـ Launcher اللي بيستقبل النتيجة من جوجل
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            account?.idToken?.let { idToken ->
                viewModel.loginWithGoogle(idToken)
            }
        } catch (e: Exception) {
            // لو المستخدم قفل شاشة جوجل أو حصل خطأ
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        TopLogoBar()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(Color.White)
                .padding(start = 20.dp, end = 20.dp, top = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,

            ) {
//            Spacer(modifier = Modifier.height(25.dp))
//
            Text(
                text = stringResource(id = R.string.signup_title),
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryRed,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(id = R.string.signup_dec),
                fontSize = 13.sp,
                fontWeight = FontWeight.W500,
                color = TextDark,
                textAlign = TextAlign.Center
            )
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
                        navController.navigate("home") {
                            // هنا بنمسح الـ register
                            popUpTo("register") { inclusive = true }
                        }
                    }
                }
                is AuthState.NeedsProfileCompletion -> {
                    LaunchedEffect(Unit) {
                        navController.navigate("complete_profile") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                }
                is AuthState.NeedsVerification -> {
                    LaunchedEffect(Unit) {
                        navController.navigate("verify_account") {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        }
                    }
                }
                else -> {}
            }

            Spacer(modifier = Modifier.height(10.dp))

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

            GovernorateCitySelector(
                selectedGovernorate = selectedGovernorate,
                selectedCity = selectedCity,
                onGovernorateSelected = { gov ->
                    selectedGovernorate = gov
                    selectedCity = "" // بنفضي المدينة عشان لو غير المحافظة، المدينة القديمة تتمسح
                },
                onCitySelected = { city ->
                    selectedCity = city
                }
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

            Spacer(modifier = Modifier.height(12.dp))

            // مربع تاريخ آخر تبرع
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { datePickerDialog.show() }
            ) {
                BloodLinkTextField(
                    value = if (lastDonationText.isEmpty()) stringResource(id = R.string.select_date_placeholder) else lastDonationText,
                    onValueChange = {}, // مش بيعمل حاجة عشان هنغيره من التقويم بس
                    label = stringResource(id = R.string.last_donation_date_label),
                    leadingIcon = Icons.Default.DateRange
                )
                // طبقة شفافة عشان نمنع الكيبورد إنه يفتح لما يضغط عليه
                Spacer(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.Transparent)
                        .clickable { datePickerDialog.show() }
                )
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
                        bloodType = selectedBlood,
                        governorate = selectedGovernorate,
                        city = selectedCity,
                        lastDonationDate = lastDonationDate
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
                    modifier = Modifier.clickable {
                        viewModel.resetState()
                        navController.popBackStack()
                    }
                )
            }


            Spacer(modifier = Modifier.height(50.dp))




        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    val navController = rememberNavController()
    RegisterScreen(navController = navController)
}