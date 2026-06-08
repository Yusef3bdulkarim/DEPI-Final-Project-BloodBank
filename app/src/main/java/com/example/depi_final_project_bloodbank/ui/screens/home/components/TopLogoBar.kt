package com.example.depi_final_project_bloodbank.ui.screens.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun TopLogoBar(onNotificationsClick: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {

        // 1. اللوجو في المنتصف تماماً (Center of the screen)
        Image(
            painter = painterResource(id = com.example.depi_final_project_bloodbank.R.drawable.bloodlink),
            contentDescription = "BloodLink Logo",
            modifier = Modifier
                .height(55.dp)
                .align(Alignment.Center), // مواءمة في السنتر بالظبط
            contentScale = ContentScale.Fit
        )

        // 2. زر الإشعارات في أقصى اليمين (End)
//        Box(
//            modifier = Modifier
//                .clickable { onNotificationsClick() }
//                .padding(4.dp)
//                .align(Alignment.CenterEnd) // مواءمة في آخر اليمين
//        ) {
//            Icon(
//                imageVector = Icons.Default.Notifications,
//                contentDescription = "Notifications",
//                modifier = Modifier.size(26.dp)
//            )
//            // النقطة (Badge)
//            Surface(
//                color = Color(0xD74DA2AF),
//                shape = CircleShape,
//                modifier = Modifier
//                    .size(9.dp)
//                    .align(Alignment.TopEnd)
//                    .offset(x = (1).dp, y = (-1).dp)
//            ) {}
//        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TopLogoBarPrev() {
    TopLogoBar()
}