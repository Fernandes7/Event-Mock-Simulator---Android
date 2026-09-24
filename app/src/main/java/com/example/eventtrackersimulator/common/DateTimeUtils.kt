package com.example.eventtrackersimulator.common

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateTimeUtils {

    @SuppressLint("ConstantLocale")
    private val timeFormatter = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())

    /** Formats an epoch-millis timestamp as e.g. "10:29:41 AM", matching the event card mockup. */
    fun formatTime(epochMillis: Long): String = timeFormatter.format(Date(epochMillis))
}
