package com.memowave.app.ui.screen.word_edit

sealed interface WordEditEvent {
    data object Load : WordEditEvent

    data class OriginalChanged(val value: String) : WordEditEvent
    data class TranslationChanged(val value: String) : WordEditEvent
    data class ExampleChanged(val value: String) : WordEditEvent
    data class CategorySelected(val categoryId: Long?) : WordEditEvent
    data class ImageChanged(val fileName: String?) : WordEditEvent

    data object Save : WordEditEvent

    data object DeleteRequested : WordEditEvent
    data object DeleteConfirmed : WordEditEvent
    data object DeleteCancelled : WordEditEvent

    data object CancelRequested : WordEditEvent
    data object CancelConfirmed : WordEditEvent
    data object CancelDismissed : WordEditEvent

    data object ResetProgressRequested : WordEditEvent
    data object UndoResetProgress : WordEditEvent
}
