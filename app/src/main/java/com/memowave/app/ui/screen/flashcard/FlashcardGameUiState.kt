package com.memowave.app.ui.screen.flashcard

import com.memowave.app.domain.model.Category
import com.memowave.app.domain.model.FlashcardResult
import com.memowave.app.domain.model.Rating
import com.memowave.app.domain.model.Word
import com.memowave.app.domain.model.WordGrade
import com.memowave.app.ui.screen.learning_shared.LearningPhase
import com.memowave.app.ui.screen.learning_shared.LearningWordSummary
import com.memowave.app.ui.screen.learning_shared.LoadState
import com.memowave.app.ui.screen.learning_shared.WordProgressDelta

enum class FlashcardGameMode {
    RECALL,
    NUMBERED
}

data class FlashcardGameUiState(
    val phase: LearningPhase = LearningPhase.LOBBY,

    val gameMode: FlashcardGameMode = FlashcardGameMode.RECALL,

    val categories: List<Category> = emptyList(),
    val categoryWordCounts: Map<Long, Int> = emptyMap(),
    val totalWordsCount: Int = 0,
    val categoriesLoadState: LoadState = LoadState.IDLE,
    val selectedCategoryId: Long? = null,
    val wordCount: Int = 10,
    val isShuffled: Boolean = true,
    val showTranslationFirst: Boolean = false,

    val words: List<Word> = emptyList(),
    val currentIndex: Int = 0,
    val isCardFlipped: Boolean = false,
    val results: List<FlashcardResult> = emptyList(),
    val summaries: List<LearningWordSummary> = emptyList(),
    val totalXpEarned: Int = 0,
    val showXpPopup: Boolean = false,
    val xpPopupAmount: Int = 0,

    val numberedOptions: List<String> = emptyList(),
    val correctAnswerIndex: Int = 0,
    val selectedAnswerIndex: Int? = null,

    val progressDelta: WordProgressDelta? = null,

    val gradePreview: Map<Rating, WordGrade>? = null,

    // SUMMARY phase: how many words remain today to keep the streak alive.
    // Snapshot taken from GetStreakStateUseCase when entering SUMMARY; 0 when the
    // daily target is already met.
    val streakWordsRemaining: Int = 0,

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
}
