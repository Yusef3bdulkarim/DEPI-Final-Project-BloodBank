package com.example.depi_final_project_bloodbank.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// utils/DateUtils.kt
fun Long.toFormattedDate(): String {
    val sdf = SimpleDateFormat("dd MMM yyyy • hh:mm a", Locale.getDefault())
    return sdf.format(Date(this))
}

fun Long.toTimeAgo(): String {
    val diff = System.currentTimeMillis() - this

    val minute = 60 * 1000
    val hour = 60 * minute
    val day = 24 * hour

    return when {
        diff < minute -> "Now"
        diff < hour -> "${diff / minute} min ago"
        diff < day -> "${diff / hour} hours ago"
        diff < day * 2 -> "Yesterday"
        else -> "${diff / day} days ago"
    }
}