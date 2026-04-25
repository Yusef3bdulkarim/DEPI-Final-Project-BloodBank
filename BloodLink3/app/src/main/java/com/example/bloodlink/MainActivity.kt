package com.example.bloodlink

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.bloodlink.navigation.AppNav
import com.example.bloodlink.ui.theme.BloodLinkTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BloodLinkTheme {
                AppNav()
            }
        }
    }
}