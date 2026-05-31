package com.vapestoreunik.madep.core.common

object RupiahFormatter {
    fun format(amount: Long): String {
        val abs = kotlin.math.abs(amount)
        val grouped = buildString {
            val s = abs.toString()
            var count = 0
            for (i in s.indices.reversed()) {
                append(s[i])
                count++
                if (count == 3 && i != 0) {
                    append('.')
                    count = 0
                }
            }
        }.reversed()
        return if (amount < 0) "-Rp $grouped" else "Rp $grouped"
    }

    fun parse(input: String): Long {
        val cleaned = input.trim()
            .replace(Regex("(?i)rp"), "")
            .replace(".", "")
            .replace(",", "")
            .trim()
        if (cleaned.isEmpty()) return 0L
        return cleaned.toLongOrNull() ?: 0L
    }
}
