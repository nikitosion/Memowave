package com.memowave.app.ui.common.category

import androidx.compose.ui.graphics.Color

/**
 * Curated category color palette. Stored as ARGB hex with alpha (ARGB) so it
 * round-trips losslessly through the backend (which gets `#FF...` strings).
 * Actual UI colors are decoded via [parseHexColor].
 */
object CategoryPalette {

    val SWATCHES: List<String> = listOf(
        "#FFEF4444", // red
        "#FFF97316", // orange
        "#FFF59E0B", // amber
        "#FF22C55E", // green
        "#FF14B8A6", // teal
        "#FF0EA5E9", // sky
        "#FF3B82F6", // blue
        "#FF6366F1", // indigo
        "#FFA855F7", // purple
        "#FFEC4899"  // pink
    )

    /** Parses a #RRGGBB / #AARRGGBB / #FFAABBCC string. Returns null on failure. */
    fun parseHexColor(hex: String?): Color? {
        if (hex.isNullOrBlank()) return null
        return runCatching { Color(android.graphics.Color.parseColor(hex)) }.getOrNull()
    }

    /** Normalize partial input: ensures leading `#`, uppercases. */
    fun normalizeHex(input: String): String {
        val cleaned = input.trim().removePrefix("#").uppercase()
        return "#$cleaned"
    }

    /** Returns true iff [hex] (with or without #) decodes to a valid Compose Color. */
    fun isValidHex(hex: String): Boolean {
        val normalized = normalizeHex(hex)
        // Allow 6 (RGB) and 8 (ARGB) digit hex.
        val digits = normalized.removePrefix("#")
        if (digits.length != 6 && digits.length != 8) return false
        return parseHexColor(normalized) != null
    }
}
