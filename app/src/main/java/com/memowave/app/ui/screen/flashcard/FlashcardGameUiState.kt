package com.memowave.app.ui.screen.flashcard

import com.memowave.app.domain.model.Category
import com.memowave.app.domain.model.FlashcardResult
import com.memowave.app.domain.model.Rating
import com.memowave.app.domain.model.Word
import com.memowave.app.domain.model.WordGrade
import java.time.LocalDateTime

enum class FlashcardPhase {
    LOBBY,
    GAME,
    SUMMARY,
    NEXT_PREP
}

enum class FlashcardGameMode {
    RECALL,
    NUMBERED
}

enum class LoadState {
    IDLE,
    LOADING,
    LOADED,
    ERROR
}

data class FlashcardGameUiState(
    // Phase
    val phase: FlashcardPhase = FlashcardPhase.LOBBY,

    // Game mode
    val gameMode: FlashcardGameMode = FlashcardGameMode.RECALL,

    // Settings
    val categories: List<Category> = emptyList(),
    val categoryWordCounts: Map<Long, Int> = emptyMap(),
    val totalWordsCount: Int = 0,
    val categoriesLoadState: LoadState = LoadState.IDLE,
    val selectedCategoryId: Long? = null,
    val wordCount: Int = 10,
    val isShuffled: Boolean = true,
    val showTranslationFirst: Boolean = false,

    // Game
    val words: List<Word> = emptyList(),
    val currentIndex: Int = 0,
    val isCardFlipped: Boolean = false,
    val results: List<FlashcardResult> = emptyList(),
    val summaries: List<FlashcardWordSummary> = emptyList(),
    val totalXpEarned: Int = 0,
    val showXpPopup: Boolean = false,
    val xpPopupAmount: Int = 0,

    // Numbered mode
    val numberedOptions: List<String> = emptyList(),
    val correctAnswerIndex: Int = 0,
    val selectedAnswerIndex: Int? = null,

    // Progress delta for the most recent answer (null when no recent answer / between cards)
    val progressDelta: WordProgressDelta? = null,

    // Preview of FSRS outcomes for each Rating, to be displayed on rating buttons.
    // null while being calculated or before card is flipped.
    val gradePreview: Map<Rating, WordGrade>? = null,

    // SUMMARY phase: how many words remain to save the current streak (placeholder until
    // the real streak feature lands — randomized once when entering SUMMARY).
    val streakWordsRemaining: Int = 0,

    // NEXT_PREP phase countdown. null means no timer (cancelled or phase not active).
    val nextPrepCountdownSeconds: Int? = null,

    // UI
    val showSettingsSheet: Boolean = false,
    val showExitConfirmation: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val currentWord: Word? get() = words.getOrNull(currentIndex)
    val progress: String get() = "${currentIndex + 1}/${words.size}"
    val correctCount: Int get() = results.count { it.isCorrect }
    val wrongCount: Int get() = results.count { !it.isCorrect }
    val isLastCard: Boolean get() = currentIndex >= words.size - 1

    val selectedCategoryWordCount: Int
        get() = if (selectedCategoryId == null) totalWordsCount
        else categoryWordCounts[selectedCategoryId] ?: 0
    val selectedCategoryHasWords: Boolean get() = selectedCategoryWordCount > 0
}

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
 * Per-card snapshot used by the SUMMARY screen to render a list with each word's
 * before/after dynamics. The `word` field carries the pre-update state so the row
 * displays the original/translation/imageUrl the user just saw.
 */
data class FlashcardWordSummary(
    val word: Word,
    val isCorrect: Boolean,
    val delta: WordProgressDelta
)
