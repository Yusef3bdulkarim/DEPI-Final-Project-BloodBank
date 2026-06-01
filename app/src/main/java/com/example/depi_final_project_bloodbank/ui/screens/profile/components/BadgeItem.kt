package com.example.depi_final_project_bloodbank.ui.screens.profile.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
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
    val badgeIcon = when (type) {
        "expert" -> R.drawable.reward
        "life" -> R.drawable.heart
        "star" -> R.drawable.star
        else -> R.drawable.star
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp)
    ) {

        Box(
            modifier = Modifier.size(64.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(badgeIcon),
                contentDescription = title,
                if (type == "expert") {
                    Modifier.size(64.dp)
                } else {
                    Modifier.size(58.dp)                }
            )
        }


        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}