


package com.example.depi_final_project_bloodbank.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.depi_final_project_bloodbank.R
import java.util.Calendar

// ── ألوان ──────────────────────────────────────────────────────────────────
private val DarkRed   = Color(0xFF8B0000)
private val BloodRed  = Color(0xFFCC1111)
private val ChipWhite = Color(0xFFFFFFFF)
private val TextGray  = Color(0xFFAAAAAA)
private val TextDark  = Color(0xFF1A1A1A)

@Composable
fun HeaderSection(
    name: String,
    bloodType: String,
) {
    val hour     = remember { Calendar.getInstance().get(Calendar.HOUR_OF_DAY) }
    val greeting = if (hour in 4..15)
        stringResource(R.string.good_morning)
    else
        stringResource(R.string.good_evening)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {

            // ── اليسار: لوجو + اسم التطبيق + ترحيب ───────────────────
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // ★ اللوجو — استبدل R.drawable.ic_logo بمورد اللوجو الحقيقي
                // استبدل الـ Surface الخاصة باللوجو بالكود ده


                // اسم التطبيق + الترحيب
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text       = "Blood Link",
                        fontSize   = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 0.3.sp
                    )
                    Text(
                        text = buildAnnotatedString {

                            withStyle(SpanStyle(color = TextGray, fontWeight = FontWeight.Medium)) {
                                append("$greeting، ")
                            }
                            withStyle(SpanStyle(color = TextDark, fontWeight = FontWeight.Bold)) {
                                append("$name!")
                            }
                        },
                        fontSize = 12.5.sp
                    )
                }
            }


            // ── اليمين: إشعارات + blood type ───────────────────────────
            Surface(
                shape           = RoundedCornerShape(12.dp),
                color           = ChipWhite,
                shadowElevation = 4.dp
                , modifier = Modifier.size(height = 45.dp, width = 45.dp),
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text          = bloodType,
                        fontSize      = 23.sp,
                        fontWeight    = FontWeight.Black,
                        color         = DarkRed,
                        letterSpacing = 0.5.sp,

                    )
                }
            }
        }

        // ── Divider خفيف ───────────────────────────────────────────────

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFFE0E0E0))
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF2F2F7)
@Composable
private fun HeaderSectionPreview() {
    MaterialTheme {
        Box(Modifier.padding(16.dp)) {
            HeaderSection(
                name            = "يوسف",
                bloodType       = "O-",

            )
        }
    }
}