package com.example.depi_final_project_bloodbank.data.locale

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LocaleManager {

    fun setLocale(context: Context, language: String): Context {
        val locale = Locale.forLanguageTag(language)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        return context.createConfigurationContext(config)
    }
}