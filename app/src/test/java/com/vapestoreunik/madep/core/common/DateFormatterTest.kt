package com.vapestoreunik.madep.core.common

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.TimeZone

class DateFormatterTest {
    private val utc = TimeZone.getTimeZone("UTC")

    @Test fun formatDisplay_returns_dd_mmm_yyyy_id_locale() {
        // 2026-06-01 00:00:00 UTC
        val millis = 1780272000000L
        assertEquals("01 Jun 2026", DateFormatter.formatDisplay(millis, utc))
    }

    @Test fun formatYyyymmdd_returns_yyyymmdd() {
        val millis = 1780272000000L
        assertEquals("20260601", DateFormatter.formatYyyymmdd(millis, utc))
    }

    @Test fun startOfDay_returns_midnight_in_tz() {
        val anytime = 1780300000000L  // some time in 2026-06-01 UTC
        val start = DateFormatter.startOfDayMillis(anytime, utc)
        assertEquals(1780272000000L, start)
    }

    @Test fun endOfDay_returns_last_millis_of_day() {
        val anytime = 1780300000000L
        val end = DateFormatter.endOfDayMillis(anytime, utc)
        assertEquals(1780272000000L + 86_400_000L - 1, end)
    }
}
