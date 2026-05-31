package com.vapestoreunik.madep.core.common

import org.junit.Assert.assertEquals
import org.junit.Test

class RupiahFormatterTest {
    @Test fun format_zero_returns_rp_0() {
        assertEquals("Rp 0", RupiahFormatter.format(0))
    }

    @Test fun format_1000_returns_rp_1_000_with_dot_separator() {
        assertEquals("Rp 1.000", RupiahFormatter.format(1000))
    }

    @Test fun format_1234567_returns_rp_1_234_567() {
        assertEquals("Rp 1.234.567", RupiahFormatter.format(1234567))
    }

    @Test fun format_negative_preserves_sign() {
        assertEquals("-Rp 5.000", RupiahFormatter.format(-5000))
    }

    @Test fun parse_with_prefix_and_dots() {
        assertEquals(1234567L, RupiahFormatter.parse("Rp 1.234.567"))
    }

    @Test fun parse_tolerant_of_whitespace_and_prefix() {
        assertEquals(75000L, RupiahFormatter.parse("  rp75.000 "))
        assertEquals(75000L, RupiahFormatter.parse("75000"))
    }

    @Test fun parse_empty_returns_zero() {
        assertEquals(0L, RupiahFormatter.parse(""))
        assertEquals(0L, RupiahFormatter.parse("   "))
    }
}
