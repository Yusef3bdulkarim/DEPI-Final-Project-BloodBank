package com.example.depi_final_project_bloodbank.data.local

import android.content.Context
import androidx.annotation.VisibleForTesting

object HealthTipPreferences {
    private const val PREFS_NAME = "settings"
    private const val KEY_TIP_INDEX = "health_tip_index"

    private var cachedIndex: Int? = null

    fun getTipIndex(context: Context, tipCount: Int): Int {
        cachedIndex?.let { return it }

        val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val lastIndex = prefs.getInt(KEY_TIP_INDEX, -1)
        val nextIndex = (lastIndex + 1) % tipCount

        prefs.edit().putInt(KEY_TIP_INDEX, nextIndex).apply()
        cachedIndex = nextIndex
        return nextIndex
    }

    @VisibleForTesting
    internal fun resetForTesting() {
        cachedIndex = null
    }
}