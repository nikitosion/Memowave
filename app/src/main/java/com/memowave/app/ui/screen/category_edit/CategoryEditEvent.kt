package com.memowave.app.ui.screen.category_edit

sealed interface CategoryEditEvent {
    data object Load : CategoryEditEvent

    data class NameChanged(val value: String) : CategoryEditEvent
    data class DescriptionChanged(val value: String) : CategoryEditEvent
    data class ColorSelected(val hex: String?) : CategoryEditEvent
    data class IconSelected(val name: String?) : CategoryEditEvent
    data class CustomColorChanged(val value: String) : CategoryEditEvent
    data object ToggleCustomColor : CategoryEditEvent

    data object Save : CategoryEditEvent

    data object DeleteRequested : CategoryEditEvent
    data object DeleteConfirmed : CategoryEditEvent
    data object DeleteCancelled : CategoryEditEvent

    data object CancelRequested : CategoryEditEvent
    data object CancelConfirmed : CategoryEditEvent
    data object CancelDismissed : CategoryEditEvent
}
