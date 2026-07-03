package com.example.depi_final_project_bloodbank.ui.screens.auth

import android.app.Activity
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel // <-- استيراد الـ viewModel
import androidx.navigation.NavController
import com.example.depi_final_project_bloodbank.R
import com.example.depi_final_project_bloodbank.components.BloodLinkButton
import com.example.depi_final_project_bloodbank.components.BloodLinkTextField
import com.example.depi_final_project_bloodbank.ui.theme.PrimaryRed
import com.example.depi_final_project_bloodbank.ui.theme.TextDark
import com.example.depi_final_project_bloodbank.ui.screens.auth.viewmodel.AuthState // <-- استيراد الـ AuthState
import com.example.depi_final_project_bloodbank.ui.screens.auth.viewmodel.AuthViewModel // <-- استيراد الـ AuthViewModel
import com.example.depi_final_project_bloodbank.ui.screens.home.components.TopLogoBar
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException


@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = viewModel()
) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(value = false) }
    val context = LocalContext.current
    val activity = context as Activity
    val sharedPref = context.getSharedPreferences("settings", Context.MODE_PRIVATE)


    // <-- مراقبة حالة الـ Auth
    val authState by viewModel.authState.collectAsState()

    val currentLocales = AppCompatDelegate.getApplicationLocales()
    val isArabic = currentLocales.toLanguageTags().contains("ar")

    // ضيف دول فوق الـ Column الأساسي

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
                viewModel.loginWithGoogle(idToken) // نبعت التوكن للـ ViewModel
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
        verticalArrangement = Arrangement.Center
    ) {
        TopLogoBar()
        Text(
            text = stringResource(id = R.string.login_title),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryRed,
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(id = R.string.login_dec),
            fontSize = 13.sp,
            fontWeight = FontWeight.W500,
            color = TextDark,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(15.dp))

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
                .clickable {
                    viewModel.resetState()
                    navController.navigate("forgot_password")
                }
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
                val errorMessage = errorState.messageId?.let { stringResource(id = it) }
                    ?: errorState.messageStr ?: ""

                Text(
                    text = errorMessage,
                    color = Color.Red,
                    textAlign = TextAlign.Center
                )
            }

            is AuthState.Success -> {
                LaunchedEffect(Unit) {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            }

            is AuthState.NeedsProfileCompletion -> {
                LaunchedEffect(Unit) {
                    android.widget.Toast.makeText(
                        context,
                        context.getString(R.string.going_to_complete_profile),
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                    navController.navigate("complete_profile") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            }

            is AuthState.NeedsVerification -> {
                LaunchedEffect(Unit) {
                    navController.navigate("verify_account") {
                        // بنمسح الشاشة من الذاكرة عشان ميرجعلهاش بالغلط
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
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

        Surface(
            modifier = Modifier
                .fillMaxWidth() // ليأخذ العرض الكامل المتناسق مع باقي الحقول كما في الصورة
                .height(56.dp)
                .width(100.dp)   // الارتفاع القياسي لحقول الإدخال والأزرار الحديثة (Standard Touch Target)
                .clip(RoundedCornerShape(12.dp)) // حواف دائرية ناعمة ومطابقة للتصميم الجديد
                .clickable { launcher.launch(googleSignInClient.signInIntent) },
            shape = RoundedCornerShape(12.dp),
            color = Color.White, // خلفية بيضاء ناصعة
            border = BorderStroke(
                width = 1.dp,
                color = Color(0xFFE0E0E0)
            ) // حدود رمادية خفيفة وناعمة
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.Center, // لتوسيط العناصر بالكامل داخل الزر
                verticalAlignment = Alignment.CenterVertically // لتوسيط العناصر عمودياً
            ) {
                // أيقونة جوجل بأبعاد متناسقة
                Image(
                    painter = painterResource(id = R.drawable.google),
                    contentDescription = "Google Sign In",
                    modifier = Modifier.size(24.dp) // حجم مثالي للأيقونة القياسية
                )

                Spacer(modifier = Modifier.width(12.dp)) // مسافة مرنة بين الأيقونة والنص

                // النص الداخلي للزر
                Text(
                    text = stringResource(R.string.continue_with_google),
                    color = Color(0xFF212121),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }


    }
}



