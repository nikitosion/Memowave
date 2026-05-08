package com.memowave.app.ui.screen.translation

sealed interface TranslationGameEvent {
    data object LoadCategories : TranslationGameEvent
    data class SelectCategory(val categoryId: Long?) : TranslationGameEvent
    data class SetWordCount(val count: Int) : TranslationGameEvent
    data class SetShuffled(val shuffled: Boolean) : TranslationGameEvent
    data class SetTypoStrictness(val strictness: TypoStrictness) : TranslationGameEvent

    data object StartGame : TranslationGameEvent
    data class UpdateInput(val value: String) : TranslationGameEvent
    data object SubmitAnswer : TranslationGameEvent
    data object ShowHintLetter : TranslationGameEvent
    data object RevealAnswer : TranslationGameEvent
    data object AdvanceCard : TranslationGameEvent
    data object DismissXpPopup : TranslationGameEvent

    data object ShowSettings : TranslationGameEvent
    data object DismissSettings : TranslationGameEvent
    data object RequestExit : TranslationGameEvent
    data object ConfirmExit : TranslationGameEvent
    data object DismissExitDialog : TranslationGameEvent

    data object EnterNextPrep : TranslationGameEvent
    data object ConfirmNextSession : TranslationGameEvent
}
