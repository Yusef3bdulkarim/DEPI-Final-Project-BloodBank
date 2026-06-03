package com.example.depi_final_project_bloodbank.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// utils/DateUtils.kt
fun Long.toFormattedDate(): String {
    val sdf = SimpleDateFormat("dd MMM yyyy • hh:mm a", Locale.getDefault())
    return sdf.format(Date(this))
}