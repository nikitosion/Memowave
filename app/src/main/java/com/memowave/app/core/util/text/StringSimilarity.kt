package com.memowave.app.core.util.text

import java.text.Normalizer

/**
 * Lightweight string-similarity utilities used by Translation mode to forgive minor
 * typos when comparing the user's typed answer against the expected translation.
 * No external dependencies.
 */
object StringSimilarity {

    /**
     * Lowercases, trims, strips diacritics (ӗ → е, é → e, ё stays as is intentionally
     * because Russian users distinguish it), collapses internal whitespace, and
     * removes punctuation. Suitable as a pre-step before similarity comparison.
     */
    fun normalize(input: String): String {
        if (input.isEmpty()) return ""
        // Don't strip ё → е: in Russian language learning, the difference matters
        // and most words use one or the other consistently.
        val lower = input.trim().lowercase()
        val noDia = Normalizer.normalize(lower, Normalizer.Form.NFD)
            .filter { it.category != CharCategory.NON_SPACING_MARK || it == '́' || it == '̈' }
        // Remove punctuation/control, keep letters, digits, whitespace and hyphens.
        val cleaned = StringBuilder(noDia.length)
        for (c in noDia) {
            when {
                c.isLetterOrDigit() -> cleaned.append(c)
                c == '-' || c == '\'' -> cleaned.append(c)
                c.isWhitespace() -> cleaned.append(' ')
                // drop all other punctuation
            }
        }
        // collapse whitespace
        return cleaned.toString().replace(Regex("\\s+"), " ").trim()
    }

    /**
     * Standard Levenshtein edit distance. O(n*m) time, O(min(n,m)) space.
     */
    fun levenshtein(a: String, b: String): Int {
        if (a == b) return 0
        if (a.isEmpty()) return b.length
        if (b.isEmpty()) return a.length

        // Ensure b is the shorter — keeps the rolling row small.
        val (s, t) = if (a.length < b.length) b to a else a to b
        val n = s.length
        val m = t.length

        var prev = IntArray(m + 1) { it }
        var curr = IntArray(m + 1)

        for (i in 1..n) {
            curr[0] = i
            val sc = s[i - 1]
            for (j in 1..m) {
                val cost = if (sc == t[j - 1]) 0 else 1
                curr[j] = minOf(
                    prev[j] + 1,        // deletion
                    curr[j - 1] + 1,    // insertion
                    prev[j - 1] + cost  // substitution
                )
            }
            val tmp = prev
            prev = curr
            curr = tmp
        }
        return prev[m]
    }

    /**
     * Normalized similarity in [0.0, 1.0]: 1 = identical, 0 = totally different.
     * Computed as 1 - distance / max(length).
     */
    fun similarity(a: String, b: String): Float {
        if (a.isEmpty() && b.isEmpty()) return 1f
        val maxLen = maxOf(a.length, b.length)
        if (maxLen == 0) return 1f
        val dist = levenshtein(a, b)
        return 1f - dist.toFloat() / maxLen
    }

    /**
     * Convenience: normalize both inputs first, then compute similarity. This is
     * what callers typically want — it forgives capitalization, surrounding
     * whitespace, punctuation and diacritic noise.
     */
    fun normalizedSimilarity(input: String, expected: String): Float {
        return similarity(normalize(input), normalize(expected))
    }
}
