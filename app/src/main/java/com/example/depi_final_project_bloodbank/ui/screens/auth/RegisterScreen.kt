package com.example.depi_final_project_bloodbank.ui.screens.auth

import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import android.net.Uri
import coil.compose.AsyncImage

import com.example.depi_final_project_bloodbank.utils.ImageUtils
import android.widget.Toast

@SuppressLint("LocalContextGetResourceValueCall")
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
    // 2. مراقبة حالة نتيجة تحليل فصيلة الدم من الـ ViewModel
    val scanResult by viewModel.scanResult.collectAsState()
    // 6. مراقبة حالة عملية التحليل لإظهار مؤشر تحميل (Loading State)
    val isScanning by viewModel.isScanning.collectAsState()
    var selectedGovernorate by remember { mutableStateOf("") }
    var selectedCity by remember { mutableStateOf("") }
    var lastDonationDate by remember { mutableStateOf<Long?>(null) }
    var lastDonationText by remember { mutableStateOf("") }
    val context = LocalContext.current

    var proofImage by remember { mutableStateOf<Any?>(null) }

    var showImageSourceDialog by remember { mutableStateOf(false) }


    // 3. تأثير جانبي (LaunchedEffect) لتحديث فصيلة الدم المختارة تلقائياً بمجرد نجاح التحليل
    LaunchedEffect(scanResult) {
        scanResult?.let { result ->
            if (result.isSuccessful && result.bloodType != null) {
                selectedBlood = result.bloodType
            }
        }
    }


    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            proofImage = bitmap
            // 4. إرسال الصورة المأخوذة من الكاميرا للـ ViewModel لتحليلها
            viewModel.scanBloodType(bitmap)
        }
    }


    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            proofImage = uri
            // 5. تحويل الصورة المختارة من المعرض إلى Bitmap ثم إرسالها للتحليل
            val bitmap = ImageUtils.compressAndOptimize(context, uri)
            bitmap?.let { viewModel.scanBloodType(it) }
        }
    }

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
            .verticalScroll(rememberScrollState())
            .padding(start = 20.dp, end = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,

        ) {
        TopLogoBar()
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
                val errorMessage =
                    errorState.messageId?.let { stringResource(id = it) } ?: errorState.messageStr
                    ?: ""
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
            onValueChange = {
                // نمنع المستخدم يكتب أي حاجة غير الأرقام، ونمنعه يكتب أكتر من 11 رقم
                if (it.all { char -> char.isDigit() } && it.length <= 11) {
                    phone = it
                }
            },
            label = stringResource(id = R.string.phone_label),
            leadingIcon = Icons.Default.Phone
        )

        // التحذير الأحمر بيظهر بس لو هو كتب أرقام بس لسه مكملش الـ 11
        if (phone.isNotEmpty() && phone.length < 11) {
            Text(
                text = stringResource(id = R.string.phone_length_error),
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, top = 4.dp)
            )
        }

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
                Text(
                    text = stringResource(id = R.string.blood_type_label),
                    style = MaterialTheme.typography.titleMedium
                )
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
                                        PrimaryRed else Color(0xDFF6DCDC),
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

        if (showImageSourceDialog) {
            AlertDialog(
                onDismissRequest = { showImageSourceDialog = false },
                title = { Text(text = stringResource(id = R.string.choose_image_source)) },
                confirmButton = {
                    TextButton(onClick = {
                        showImageSourceDialog = false
                        cameraLauncher.launch(null) // يفتح الكاميرا
                    }) {
                        Text(text = stringResource(id = R.string.camera), color = PrimaryRed)
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showImageSourceDialog = false
                        galleryLauncher.launch("image/*") // يفتح المعرض
                    }) {
                        Text(text = stringResource(id = R.string.gallery), color = PrimaryRed)
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(12.dp))
                .border(
                    width = 1.dp,
                    color = if (proofImage != null) Color(0xFF006400) else Color.Gray,
                    shape = RoundedCornerShape(12.dp)
                )
                // 7. تعطيل الضغط على الزر أثناء عملية التحليل لمنع التداخل
                .clickable(enabled = !isScanning) { showImageSourceDialog = true }
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            // 8. عرض مؤشر تحميل (CircularProgressIndicator) إذا كانت عملية التحليل جارية
            if (isScanning) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = PrimaryRed,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stringResource(id = R.string.scanning_image),
                        color = TextDark,
                        fontSize = 14.sp
                    )
                }
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (proofImage != null) {
                        // مكتبة Coil هتعرض الصورة أوتوماتيك سواء كانت Uri أو Bitmap
                        AsyncImage(
                            model = proofImage,
                            contentDescription = "Proof Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        scanResult?.errorMessage?.let {
                            Text(
                                text = it,
                                color = if (scanResult!!.isSuccessful) {
                                    Color(0xFF006400)
                                } else {
                                    Color(0xFF8B0000)
                                },
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = null,
                            tint = PrimaryRed
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = stringResource(id = R.string.upload_proof_instruction),
                            color = TextDark,
                            fontSize = 14.sp
                        )
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

        // الملحوظة بتاعت الـ 3 شهور
        Text(
            text = stringResource(id = R.string.donation_date_note),
            color = Color.Gray,
            fontSize = 12.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, top = 4.dp, bottom = 8.dp)
        )


        Spacer(modifier = Modifier.height(20.dp))

        BloodLinkButton(
            text = stringResource(id = R.string.register_button),
            onClick = {
                if (phone.length == 11 && proofImage != null) {

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


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    val navController = rememberNavController()
    RegisterScreen(navController = navController)
}