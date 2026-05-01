package com.example.depi_final_project_bloodbank.ui.screens.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.depi_final_project_bloodbank.ui.common_components.BloodActionBtn


@Composable
fun ActionButtonSection() {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(modifier = Modifier.weight(1f)) {
            BloodActionBtn(text = "REQUEST BLOOD ", isPrimary = false, onClick = {})
        }
        Box(modifier = Modifier.weight(1f)) {
            BloodActionBtn(text = "DONATE NOW", isPrimary = true, onClick = {})
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ActionButtonSectionPrev() {
    ActionButtonSection()
}