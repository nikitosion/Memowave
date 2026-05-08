package com.memowave.app.ui.screen.quiz

import com.memowave.app.domain.model.Category
import com.memowave.app.domain.model.FlashcardResult
import com.memowave.app.domain.model.Rating
import com.memowave.app.domain.model.Word
import com.memowave.app.ui.screen.learning_shared.LearningPhase
import com.memowave.app.ui.screen.learning_shared.LearningWordSummary
import com.memowave.app.ui.screen.learning_shared.LoadState
import com.memowave.app.ui.screen.learning_shared.WordProgressDelta

data class QuizGameUiState(
    val phase: LearningPhase = LearningPhase.LOBBY,

    val categories: List<Category> = emptyList(),
    val categoryWordCounts: Map<Long, Int> = emptyMap(),
    val totalWordsCount: Int = 0,
    val categoriesLoadState: LoadState = LoadState.IDLE,
    val selectedCategoryId: Long? = null,
    val isShuffled: Boolean = true,

    val totalDurationSeconds: Int = 60,
    val remainingMillis: Long = 60_000L,

    val wordPool: List<Word> = emptyList(),
    val currentWord: Word? = null,
    val options: List<String> = emptyList(),
    val correctOptionIndex: Int = 0,
    val selectedOptionIndex: Int? = null,
    val questionStartMillis: Long = 0L,
    val streak: Int = 0,

    val results: List<FlashcardResult> = emptyList(),
    val summaries: List<LearningWordSummary> = emptyList(),
    val totalXpEarned: Int = 0,
    val showXpPopup: Boolean = false,
    val xpPopupAmount: Int = 0,

    val progressDelta: WordProgressDelta? = null,

    val nextPrepCountdownSeconds: Int? = null,

    val showSettingsSheet: Boolean = false,
    val showExitConfirmation: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val correctCount: Int get() = results.count { it.isCorrect }
    val wrongCount: Int get() = results.count { !it.isCorrect }
    val totalAnswered: Int get() = results.size
    val selectedCategoryWordCount: Int
        get() = if (selectedCategoryId == null) totalWordsCount
        else categoryWordCounts[selectedCategoryId] ?: 0
    val selectedCategoryHasWords: Boolean get() = selectedCategoryWordCount > 0
    val canStart: Boolean get() = selectedCategoryWordCount >= 4
}
