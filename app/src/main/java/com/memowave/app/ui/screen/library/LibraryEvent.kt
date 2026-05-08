package com.memowave.app.ui.screen.library

import com.memowave.app.domain.model.Category
import com.memowave.app.domain.model.Word

sealed interface LibraryEvent {
    data object Load : LibraryEvent
    data class SearchChanged(val query: String) : LibraryEvent
    data class CategorySelected(val categoryId: Long?) : LibraryEvent

    /** Word events — navigation handled by [LibraryRoute]. */
    data object AddWordClicked : LibraryEvent
    data class EditWordClicked(val word: Word) : LibraryEvent

    /** Category events — currently routed through dialog. */
    data object AddCategoryClicked : LibraryEvent
    data class EditCategoryClicked(val category: Category) : LibraryEvent
    data class DeleteCategoryClicked(val categoryId: Long) : LibraryEvent
    data object DismissCategoryDialog : LibraryEvent
    data class SaveCategory(
        val name: String,
        val description: String?,
        val colorHex: String?
    ) : LibraryEvent
}
