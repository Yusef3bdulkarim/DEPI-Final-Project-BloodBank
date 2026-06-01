package com.example.depi_final_project_bloodbank.ui.screens.request

import android.content.Intent
import android.net.Uri
import android.text.format.DateUtils
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.depi_final_project_bloodbank.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestDetailsScreen(viewModel: RequestViewModel, onBackClick: () -> Unit) {
    val request by viewModel.request.collectAsState()
    val context = LocalContext.current

    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val shapes = MaterialTheme.shapes

    val timeAgo = DateUtils.getRelativeTimeSpanString(
        request.timestamp,
        System.currentTimeMillis(),
        DateUtils.MINUTE_IN_MILLIS
    ).toString()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {

        // ==========================================
        // 1. الجزء الثابت (شريط العلوي)
        // ==========================================
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = colorScheme.onSurface,
                    modifier = Modifier.size(28.dp).clickable { onBackClick() }
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = stringResource(R.string.request_details_title),
                    style = typography.titleLarge,
                    color = colorScheme.onSurface
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = colorScheme.onSurface, modifier = Modifier.size(28.dp))
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // ==========================================
        // 2. الجزء المتحرك (محتوى الشاشة)
        // ==========================================
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {

            // --- Blood Type Card ---
            Card(
                shape = shapes.medium,
                colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                border = BorderStroke(1.dp, Color(0xFFF0F0F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        val urgencyColor = when (request.urgency) {
                            "Very Urgent" -> colorScheme.error
                            "Urgent" -> colorScheme.primary
                            else -> colorScheme.tertiary
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.background(urgencyColor.copy(alpha = 0.1f), shapes.small).padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("*", color = urgencyColor, style = typography.titleMedium)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(request.urgency.uppercase(), color = urgencyColor, style = typography.labelSmall)
                        }
                    }
                    Text(stringResource(R.string.blood_type), style = typography.bodyLarge, color = Color.Gray)
                    Text(request.bloodType, color = colorScheme.primary, style = typography.titleLarge.copy(fontSize = 48.sp))
                    Text("${request.units} " + stringResource(R.string.units_of_blood), style = typography.bodyLarge, color = Color.DarkGray)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // --- Hospital Location Card ---
            Card(
                shape = shapes.medium,
                colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                border = BorderStroke(1.dp, Color(0xFFF0F0F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(40.dp).background(colorScheme.background, shapes.small), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.LocalHospital, contentDescription = null, tint = colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(stringResource(R.string.hospital_location), style = typography.labelSmall, color = Color.Gray)
                        Text(request.hospitalName.ifEmpty { stringResource(R.string.hospital_name) }, style = typography.titleMedium, color = colorScheme.onSurface)
                        Text(request.city.ifEmpty { stringResource(R.string.city_area) }, style = typography.bodyLarge, color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // --- Status & Date Row ---
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Card(
                    shape = shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                    border = BorderStroke(1.dp, Color(0xFFF0F0F0)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(stringResource(R.string.request_status), style = typography.labelSmall, color = Color.Gray)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).background(colorScheme.tertiary, CircleShape))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(stringResource(R.string.active_now), style = typography.titleMedium, color = colorScheme.onSurface)
                        }
                    }
                }
                Card(
                    shape = shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                    border = BorderStroke(1.dp, Color(0xFFF0F0F0)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(stringResource(R.string.post_date), style = typography.labelSmall, color = Color.Gray)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(if (timeAgo == "0 minutes ago") stringResource(R.string.just_now) else timeAgo, style = typography.titleMedium, color = colorScheme.onSurface)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // --- Contact Information Card ---
            Card(
                shape = shapes.medium,
                colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                border = BorderStroke(1.dp, Color(0xFFF0F0F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(48.dp).background(colorScheme.secondary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("AA", color = Color.White, style = typography.titleMedium)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(stringResource(R.string.contact_info), style = typography.labelSmall, color = Color.Gray)
                        Text(request.contactName.ifEmpty { stringResource(R.string.patient_representative) }, style = typography.titleMedium, color = colorScheme.onSurface)
                        Text(stringResource(R.string.patient_representative), style = typography.labelSmall, color = Color.Gray)
                    }
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(colorScheme.background, CircleShape)
                            .clickable {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${request.contactPhone}"))
                                context.startActivity(intent)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Call, contentDescription = "Call", tint = colorScheme.primary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Bottom Buttons ---
            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
                Button(
                    onClick = { /* سيتم برمجته لاحقاً */ },
                    colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary),
                    shape = shapes.medium,
                    modifier = Modifier.weight(1f).height(56.dp)
                ) {
                    Icon(Icons.Default.FavoriteBorder, contentDescription = null, tint = colorScheme.onPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.fulfill_request_btn), color = colorScheme.onPrimary, style = typography.titleMedium)
                }
                Spacer(modifier = Modifier.width(12.dp))
                OutlinedButton(
                    onClick = {
                        val shareText = "حالة عاجلة: محتاجين متبرع بفصيلة دم ${request.bloodType} في مستشفى ${request.hospitalName} (${request.city}).\nللتواصل: ${request.contactPhone}\n#تطبيق_BloodLink"
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, shareText)
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, "شارك")
                        context.startActivity(shareIntent)
                    },
                    shape = shapes.medium,
                    border = BorderStroke(1.dp, colorScheme.primary),
                    modifier = Modifier.size(56.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = "Share", tint = colorScheme.primary)
                }
            }
        }
    }
}