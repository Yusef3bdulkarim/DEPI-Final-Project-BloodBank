package com.example.depi_final_project_bloodbank.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.depi_final_project_bloodbank.R

@Composable

fun LogoHeader(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.bloodlink),
        contentDescription = null,
        modifier = modifier.size(120.dp),
        contentScale = ContentScale.Fit
    )
}
