package com.memowave.app.ui.screen.flashcard

sealed interface FlashcardGameEvent {
    // Settings
    data class ChangeGameMode(val gameMode: FlashcardGameMode) : FlashcardGameEvent
    data object LoadCategories : FlashcardGameEvent
    data class SelectCategory(val categoryId: Long?) : FlashcardGameEvent
    data class SetWordCount(val count: Int) : FlashcardGameEvent
    data class SetShuffled(val shuffled: Boolean) : FlashcardGameEvent
    data class SetShowTranslationFirst(val value: Boolean) : FlashcardGameEvent

    // Game flow
    data object StartGame : FlashcardGameEvent
    data object FlipCard : FlashcardGameEvent
    data object MarkCorrect : FlashcardGameEvent
    data object MarkWrong : FlashcardGameEvent
    data class SelectAnswer(val index: Int) : FlashcardGameEvent
    data object AdvanceCard : FlashcardGameEvent
    data object DismissXpPopup : FlashcardGameEvent

    // Navigation
    data object RestartGame : FlashcardGameEvent
    data object ToggleSettings : FlashcardGameEvent
    data object RequestExit : FlashcardGameEvent
    data object ConfirmExit : FlashcardGameEvent
    data object DismissExitDialog : FlashcardGameEvent
}
