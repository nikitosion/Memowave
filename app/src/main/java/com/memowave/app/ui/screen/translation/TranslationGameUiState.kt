package com.memowave.app.ui.screen.translation

import com.memowave.app.domain.model.Category
import com.memowave.app.domain.model.FlashcardResult
import com.memowave.app.domain.model.Rating
import com.memowave.app.domain.model.Word
import com.memowave.app.domain.model.WordGrade
import com.memowave.app.ui.screen.learning_shared.LearningPhase
import com.memowave.app.ui.screen.learning_shared.LearningWordSummary
import com.memowave.app.ui.screen.learning_shared.LoadState
import com.memowave.app.ui.screen.learning_shared.WordProgressDelta

/**
 * How forgiving the input check should be when there are typos.
 *  - STRICT: only exact matches count as correct (after normalization)
 *  - NORMAL: small typos (≥85% similarity) count as Hard, exact = Good/Easy
 *  - LENIENT: substantial typos (≥75% similarity) still count as Hard
 */
enum class TypoStrictness(val correctThreshold: Float, val partialThreshold: Float) {
    STRICT(correctThreshold = 0.95f, partialThreshold = 0.80f),
    NORMAL(correctThreshold = 0.85f, partialThreshold = 0.60f),
    LENIENT(correctThreshold = 0.75f, partialThreshold = 0.50f)
}

data class TranslationCheckResult(
    val isCorrect: Boolean,
    val similarity: Float,
    val correctAnswer: String,
    val rating: Rating,
    val timeMillis: Long
)

data class TranslationGameUiState(
    val phase: LearningPhase = LearningPhase.LOBBY,

    val categories: List<Category> = emptyList(),
    val categoryWordCounts: Map<Long, Int> = emptyMap(),
    val totalWordsCount: Int = 0,
    val categoriesLoadState: LoadState = LoadState.IDLE,
    val selectedCategoryId: Long? = null,
    val wordCount: Int = 10,
    val isShuffled: Boolean = true,

    val typoStrictness: TypoStrictness = TypoStrictness.NORMAL,

    val words: List<Word> = emptyList(),
    val currentIndex: Int = 0,

    val userInput: String = "",
    val hintLettersShown: Int = 0,
    val answerRevealed: Boolean = false,
    val checkResult: TranslationCheckResult? = null,
    val questionStartMillis: Long = 0L,

    val results: List<FlashcardResult> = emptyList(),
    val summaries: List<LearningWordSummary> = emptyList(),
    val totalXpEarned: Int = 0,
    val showXpPopup: Boolean = false,
    val xpPopupAmount: Int = 0,

    val progressDelta: WordProgressDelta? = null,
    val gradePreview: Map<Rating, WordGrade>? = null,

    val nextPrepCountdownSeconds: Int? = null,

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
    val isAwaitingFeedback: Boolean get() = checkResult != null
}
