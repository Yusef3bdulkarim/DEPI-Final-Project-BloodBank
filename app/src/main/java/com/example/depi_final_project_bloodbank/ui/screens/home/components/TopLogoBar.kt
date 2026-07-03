package com.example.depi_final_project_bloodbank.ui.screens.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.depi_final_project_bloodbank.R

@Composable
fun TopLogoBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
    ) {

        // 1. اللوجو في المنتصف تماماً (Center of the screen)
        Image(
            painter = painterResource(R.drawable.png),
            contentDescription = "BloodLink Logo",
            modifier = Modifier
                .size(112.dp)
                .align(Alignment.Center), // مواءمة في السنتر بالظبط
            contentScale = ContentScale.Fit
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TopLogoBarPrev() {
    TopLogoBar()
}