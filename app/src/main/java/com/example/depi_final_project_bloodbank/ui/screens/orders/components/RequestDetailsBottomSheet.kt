package com.example.depi_final_project_bloodbank.ui.screens.orders.components

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.example.depi_final_project_bloodbank.domain.enums.RequestPriority
import com.example.depi_final_project_bloodbank.domain.enums.RequestStatus
import com.example.depi_final_project_bloodbank.domain.model.BloodRequest
import com.example.depi_final_project_bloodbank.ui.theme.PrimaryRed
import com.example.depi_final_project_bloodbank.utils.toFormattedDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestDetailsBottomSheet(
    request: BloodRequest,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val isUrgent = request.priority == RequestPriority.URGENT
    val context = LocalContext.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.background,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(align = Alignment.Bottom)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // --- كارد بيانات الفصيلة ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (isUrgent) {
                        Surface(
                            color = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                            shape = CircleShape
                        ) {
                            Text(
                                "عاجل جداً",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    Text(
                        "فصيلة الدم المطلوبة",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                    Text(
                        text = request.bloodType,
                        style = MaterialTheme.typography.headlineLarge,
                        color = if (isUrgent) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 48.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Bloodtype,
                            null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${request.unitsReserved}/${request.unitsNeeded} units",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // --- كارد موقع المستشفى (تم تعديل الـ onClick لتشغيل الخرائط) ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "موقع المستشفى",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            request.hospitalName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Button(
                        onClick = {
                            // إنشاء الـ URI الخاص بخرائط جوجل وتمرير الإحداثيات واسم المستشفى كـ Label
                            val mapUri =
                                "geo:${request.hospitalLat},${request.hospitalLng}?q=${request.hospitalLat},${request.hospitalLng}(${request.hospitalName})".toUri()
                            val mapIntent = Intent(Intent.ACTION_VIEW, mapUri).apply {
                                setPackage("com.google.android.apps.maps")
                            }
                            try {
                                context.startActivity(mapIntent)
                            } catch (e: Exception) {
                                // حل احتياطي في حال عدم وجود تطبيق الخرائط (يفتح خرائط جوجل عبر المتصفح)
                                val browserIntent = Intent(
                                    Intent.ACTION_VIEW,
                                    "https://www.google.com/maps/search/?api=1&query=${request.hospitalLat},${request.hospitalLng}".toUri()
                                )
                                context.startActivity(browserIntent)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Go to Hospital", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            // --- كارد حالة الطلب والتاريخ ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val (statusText, statusDotColor) = when (request.status) {
                    RequestStatus.ACTIVE -> "Active" to Color.Green
                    RequestStatus.COMPLETED -> "Completed" to Color.Blue
                    RequestStatus.CANCELLED -> "Cancelled" to PrimaryRed
                    RequestStatus.EXPIRED -> "Expired" to Color.Gray
                }
                InfoBox(
                    label = "حالة الطلب",
                    value = statusText,
                    modifier = Modifier.weight(1f),
                    showDot = true,
                    dotColor = statusDotColor
                )
                InfoBox(
                    label = "تاريخ النشر",
                    value = request.createdAt.toFormattedDate(),
                    modifier = Modifier.weight(1f)
                )
            }

            // --- كارد بيانات المتصل والاتصال الحقيقي ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            val intent =
                                Intent(Intent.ACTION_DIAL, "tel:${request.contactPhone}".toUri())
                            context.startActivity(intent)
                        },
                        modifier = Modifier.background(
                            MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                            CircleShape
                        )
                    ) {
                        Icon(Icons.Default.Call, null, tint = MaterialTheme.colorScheme.error)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = request.contactName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = request.contactPhone,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = request.contactName.firstOrNull()?.toString() ?: "أ",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InfoBox(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    showDot: Boolean = false,
    dotColor: Color = Color.Red
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.End) {
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (showDot) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(dotColor)
                    ); Spacer(Modifier.width(6.dp))
                }
                Text(
                    value,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}