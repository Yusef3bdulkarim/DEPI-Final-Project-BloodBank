package com.example.depi_final_project_bloodbank

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.depi_final_project_bloodbank.data.locale.LocaleManager
import com.example.depi_final_project_bloodbank.navigation.AppNav
import com.example.depi_final_project_bloodbank.ui.theme.DEPIFinalProjectBloodBankTheme
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        enableEdgeToEdge()
        setContent {
            DEPIFinalProjectBloodBankTheme {
                AppNav()
            }
        }
    }
    override fun attachBaseContext(newBase: Context) {
        val sharedPref = newBase.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val lang = sharedPref.getString("lang", null)
            ?: if (java.util.Locale.getDefault().language == "ar") "ar" else "en"

        val context = LocaleManager.setLocale(newBase, lang)
        super.attachBaseContext(context)
    }
}