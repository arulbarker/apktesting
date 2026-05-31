package com.vapestoreunik.madep.core.common

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object DateFormatter {
    private val ID = Locale("id", "ID")

    fun formatDisplay(epochMillis: Long, tz: TimeZone = TimeZone.getDefault()): String =
        SimpleDateFormat("dd MMM yyyy", ID).apply { timeZone = tz }.format(Date(epochMillis))

    fun formatTime(epochMillis: Long, tz: TimeZone = TimeZone.getDefault()): String =
        SimpleDateFormat("HH:mm", ID).apply { timeZone = tz }.format(Date(epochMillis))

    fun formatYyyymmdd(epochMillis: Long, tz: TimeZone = TimeZone.getDefault()): String =
        SimpleDateFormat("yyyyMMdd", ID).apply { timeZone = tz }.format(Date(epochMillis))

    fun startOfDayMillis(epochMillis: Long, tz: TimeZone = TimeZone.getDefault()): Long {
        val cal = Calendar.getInstance(tz).apply {
            timeInMillis = epochMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }

    fun endOfDayMillis(epochMillis: Long, tz: TimeZone = TimeZone.getDefault()): Long =
        startOfDayMillis(epochMillis, tz) + 86_400_000L - 1
}
