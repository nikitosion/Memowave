package com.memowave.app.ui.screen.learning_shared

import com.memowave.app.domain.model.Word
import java.time.LocalDateTime

/**
 * Captures before/after FSRS metrics for a word that was just answered.
 * Used by SUMMARY screens (delta blocks, interval-trend chips).
 */
data class WordProgressDelta(
    val wasNew: Boolean,
    val oldStability: Double,
    val newStability: Double,
    val oldDifficulty: Double,
    val newDifficulty: Double,
    val oldInterval: Int,
    val newInterval: Int,
    val oldDueDate: LocalDateTime,
    val newDueDate: LocalDateTime
) {
    companion object {
        fun from(old: Word, new: Word, wasNew: Boolean) = WordProgressDelta(
            wasNew = wasNew,
            oldStability = old.stability,
            newStability = new.stability,
            oldDifficulty = old.difficulty,
            newDifficulty = new.difficulty,
            oldInterval = old.interval,
            newInterval = new.interval,
            oldDueDate = old.dueDate,
            newDueDate = new.dueDate
        )
    }
}

/**
 * Per-card snapshot used by SUMMARY screens to render a list with each word's
 * before/after dynamics. The `word` field carries the pre-update state so the row
 * displays the original/translation/imageUrl the user just saw.
 */
data class LearningWordSummary(
    val word: Word,
    val isCorrect: Boolean,
    val delta: WordProgressDelta
)
