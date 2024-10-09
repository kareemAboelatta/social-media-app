package com.example.common.ui.utils

import android.content.Context
import android.text.format.DateUtils
import com.example.common.R
import java.util.*
import java.util.concurrent.TimeUnit

fun Context.getRelativeTimeSpan(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp

    return when {
        diff < DateUtils.MINUTE_IN_MILLIS -> {
            // Less than a minute ago
            getString(R.string.time_just_now)
        }
        diff < DateUtils.HOUR_IN_MILLIS -> {
            // Less than an hour ago
            val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
            resources.getQuantityString(R.plurals.time_minutes_ago, minutes.toInt(), minutes.toInt())
        }
        diff < DateUtils.DAY_IN_MILLIS -> {
            // Less than a day ago
            val hours = TimeUnit.MILLISECONDS.toHours(diff)
            resources.getQuantityString(R.plurals.time_hours_ago, hours.toInt(), hours.toInt())
        }
        diff < DateUtils.WEEK_IN_MILLIS -> {
            // Less than a week ago
            val days = TimeUnit.MILLISECONDS.toDays(diff)
            resources.getQuantityString(R.plurals.time_days_ago, days.toInt(), days.toInt())
        }
        diff < DateUtils.YEAR_IN_MILLIS -> {
            // Less than a year ago
            val months = diff / (DateUtils.DAY_IN_MILLIS * 30)
            if (months < 1) {
                val weeks = diff / DateUtils.WEEK_IN_MILLIS
                resources.getQuantityString(R.plurals.time_weeks_ago, weeks.toInt(), weeks.toInt())
            } else {
                resources.getQuantityString(R.plurals.time_months_ago, months.toInt(), months.toInt())
            }
        }
        else -> {
            // More than a year ago
            val years = diff / DateUtils.YEAR_IN_MILLIS
            resources.getQuantityString(R.plurals.time_years_ago, years.toInt(), years.toInt())
        }
    }
}
