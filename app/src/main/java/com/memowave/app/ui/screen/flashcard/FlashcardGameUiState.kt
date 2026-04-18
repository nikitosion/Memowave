package com.memowave.app.ui.screen.flashcard

import com.memowave.app.domain.model.Category
import com.memowave.app.domain.model.FlashcardResult
import com.memowave.app.domain.model.Word

enum class FlashcardPhase {
    LOBBY,
    GAME,
    SUMMARY
}

data class FlashcardGameUiState(
    // Phase
    val phase: FlashcardPhase = FlashcardPhase.LOBBY,

    // Settings
    val categories: List<Category> = emptyList(),
    val selectedCategoryId: Long? = null,
    val wordCount: Int = 10,
    val isShuffled: Boolean = true,
    val showTranslationFirst: Boolean = false,

    // Game
    val words: List<Word> = emptyList(),
    val currentIndex: Int = 0,
    val isCardFlipped: Boolean = false,
    val results: List<FlashcardResult> = emptyList(),
    val totalXpEarned: Int = 0,
    val showXpPopup: Boolean = false,
    val xpPopupAmount: Int = 0,

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
}
