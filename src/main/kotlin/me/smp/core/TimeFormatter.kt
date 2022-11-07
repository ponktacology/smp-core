package me.smp.core

import java.text.SimpleDateFormat
import java.util.*

object TimeFormatter {

    private val dateFormat = SimpleDateFormat("HH:mm:ss dd-MM-yyyy")

    fun formatDate(millis: Long): String {
        return dateFormat.format(Date(millis))
    }

    fun formatCompact(millis: Long): String {
        if (millis == -1L) {
            return "never"
        }
        var seconds = millis / 1000L
        if (seconds <= 0) {
            return "now"
        }
        var minutes = seconds / 60
        seconds %= 60
        var hours = minutes / 60
        minutes %= 60
        var day = hours / 24
        hours %= 24
        val years = day / 365
        day %= 365
        val time = StringBuilder()
        if (years != 0L) {
            time.append(years).append("r")
        }
        if (day != 0L) {
            time.append(day).append("d")
        }
        if (hours != 0L) {
            time.append(hours).append("h")
        }
        if (minutes != 0L) {
            time.append(minutes).append("m")
        }
        if (seconds != 0L) {
            time.append(seconds).append("s")
        }
        return time.toString().trim { it <= ' ' }
    }
}