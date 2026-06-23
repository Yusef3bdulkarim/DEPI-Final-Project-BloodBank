package com.example.depi_final_project_bloodbank.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.depi_final_project_bloodbank.components.LogoHeader
import com.example.depi_final_project_bloodbank.ui.screens.auth.viewmodel.AuthViewModel
import com.example.depi_final_project_bloodbank.R
import com.example.depi_final_project_bloodbank.components.BloodLinkButton
import com.example.depi_final_project_bloodbank.components.BloodLinkTextField
import com.example.depi_final_project_bloodbank.ui.theme.PrimaryRed
import com.example.depi_final_project_bloodbank.ui.screens.auth.viewmodel.AuthState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.depi_final_project_bloodbank.ui.common_components.GovernorateCitySelector

@Composable
fun CompleteProfileScreen(
    navController: NavController,
    uid: String, // الـ UID اللي جاي من جوجل
    viewModel: AuthViewModel = viewModel()
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
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





    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        LogoHeader()
        Text(
            text = stringResource(id = R.string.complete_profile_title), // "استكمال بيانات الحساب"
            fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryRed,
            modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(30.dp))

        // الاسم (عشان لو حابب يغير الاسم اللي جاي من جوجل)
        BloodLinkTextField(
            value = name, onValueChange = { name = it },
            label = stringResource(id = R.string.name_label),
            leadingIcon = Icons.Default.Person
        )

        Spacer(modifier = Modifier.height(15.dp))

        // رقم الهاتف
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

        Spacer(modifier = Modifier.height(20.dp))

        GovernorateCitySelector(
            selectedGovernorate = selectedGovernorate,
            selectedCity = selectedCity,
            onGovernorateSelected = { gov ->
                selectedGovernorate = gov
                selectedCity = ""
            },
            onCitySelected = { city ->
                selectedCity = city
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // فصيلة الدم
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(12.dp))
                .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.drop),
                        contentDescription = null,
                        modifier = Modifier.size(25.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(id = R.string.blood_type_label),
                        style = MaterialTheme.typography.titleMedium
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
                                    if (selectedBlood == type) PrimaryRed else Color(0xDFF6DCDC),
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedBlood = type }
                                .padding(horizontal = 10.dp, vertical = 6.dp)) {
                            Text(
                                text = type,
                                fontSize = 12.sp,
                                color = if (selectedBlood == type) Color.White else Color.Black
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
        // الملحوظة بتاعت الـ 3 شهور
        Text(
            text = stringResource(id = R.string.donation_date_note),
            color = Color.Gray,
            fontSize = 12.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, top = 4.dp, bottom = 8.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        BloodLinkButton(
            text = stringResource(id = R.string.save_and_continue), // "حفظ ومتابعة"
            onClick = {
                if (phone.length == 11) {
                    viewModel.completeProfile(
                        uid,
                        name,
                        phone,
                        selectedBlood,
                        selectedGovernorate,
                        selectedCity,
                        lastDonationDate
                    )
                }
            }
        )

        // Handling Loading/Error/Success
        if (authState is AuthState.Loading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = PrimaryRed
            )
        }
        if (authState is AuthState.Error) {
            val error = authState as AuthState.Error
            val msg = error.messageId?.let { stringResource(it) } ?: error.messageStr ?: ""
            Text(
                text = msg,
                color = Color.Red,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
        if (authState is AuthState.Success) {
            LaunchedEffect(Unit) {
                navController.navigate("home") { popUpTo("complete_profile") { inclusive = true } }
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true, locale = "ar")
@Composable
fun CompleteProfileScreenPreview() {
    // إنشاء NavController وهمي خاص بالـ Preview
    val navController = rememberNavController()

    // استدعاء الشاشة مع تمرير بيانات وهمية
    CompleteProfileScreen(
        navController = navController,
        uid = "dummy_uid_123"
        // مش محتاجين نمرر الـ ViewModel لأنه بياخد قيمة افتراضية
    )
}