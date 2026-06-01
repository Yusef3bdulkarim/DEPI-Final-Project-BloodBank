package com.example.depi_final_project_bloodbank

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.depi_final_project_bloodbank.navigation.AppNav
import com.example.depi_final_project_bloodbank.ui.theme.DEPIFinalProjectBloodBankTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DEPIFinalProjectBloodBankTheme {
                AppNav()
            }
        }
    }
}