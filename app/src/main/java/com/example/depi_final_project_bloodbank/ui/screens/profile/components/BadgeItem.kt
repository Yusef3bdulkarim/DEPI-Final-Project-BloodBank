package com.example.depi_final_project_bloodbank.ui.screens.profile.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.depi_final_project_bloodbank.R

@Composable
fun BadgeItem(
    title: String,
    type: String,
) {
    val badge = when (type) {
        "expert" -> R.drawable.reward
        "life" -> R.drawable.heart
        "star" -> R.drawable.star
        else -> R.drawable.star
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .width(60.dp)
                .height(60.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(badge),
                contentDescription = "Reward",
                Modifier.size(60.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
