package com.example.depi_final_project_bloodbank.ui.common_components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.depi_final_project_bloodbank.ui.theme.BloodRed


@Composable
fun BloodTypeChip(
    bloodType: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.size(50.dp),
        shape = CircleShape,
        color = Color.White,
        border = BorderStroke(1.dp, Color.LightGray)
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = bloodType,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = BloodRed // اللون الأحمر الذي عرفناه سابقاً
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BloodTypeChipPrev() {
    BloodTypeChip(
        bloodType = "O+"
    )
}