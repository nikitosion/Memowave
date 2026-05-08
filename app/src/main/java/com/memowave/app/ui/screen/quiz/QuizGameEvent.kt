package com.memowave.app.ui.screen.quiz

sealed interface QuizGameEvent {
    data object LoadCategories : QuizGameEvent
    data class SelectCategory(val categoryId: Long?) : QuizGameEvent
    data class SetShuffled(val shuffled: Boolean) : QuizGameEvent
    data class SetDurationSeconds(val seconds: Int) : QuizGameEvent

    data object StartGame : QuizGameEvent
    data class SelectOption(val index: Int) : QuizGameEvent
    data object DismissXpPopup : QuizGameEvent

    data object ShowSettings : QuizGameEvent
    data object DismissSettings : QuizGameEvent
    data object RequestExit : QuizGameEvent
    data object ConfirmExit : QuizGameEvent
    data object DismissExitDialog : QuizGameEvent

    data object EnterNextPrep : QuizGameEvent
    data object ConfirmNextSession : QuizGameEvent
}
