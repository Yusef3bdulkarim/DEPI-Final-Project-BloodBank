package com.example.depi_final_project_bloodbank

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.example.depi_final_project_bloodbank.navigation.AppNav
import com.example.depi_final_project_bloodbank.ui.theme.DEPIFinalProjectBloodBankTheme


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DEPIFinalProjectBloodBankTheme {
                AppNav()
            }
        }
    }
}
