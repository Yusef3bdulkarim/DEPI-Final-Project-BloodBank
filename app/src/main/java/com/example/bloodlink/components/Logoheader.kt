package com.example.bloodlink.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.bloodlink.R

@Composable
fun LogoHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
    ) {
        Image(
            painter = painterResource(id = R.drawable.bloodlink),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 12.dp)
                .size(180.dp),
            contentScale = ContentScale.Fit
        )
    }
}
