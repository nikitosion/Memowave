package com.memowave.app.ui.screen.library

import com.memowave.app.domain.model.Category
import com.memowave.app.domain.model.Word

sealed interface LibraryEvent {
    data object Load : LibraryEvent
    data class SearchChanged(val query: String) : LibraryEvent
    data class CategorySelected(val categoryId: Long?) : LibraryEvent

    /** Navigation triggers — handled by [LibraryRoute], not the ViewModel. */
    data object AddWordClicked : LibraryEvent
    data class EditWordClicked(val word: Word) : LibraryEvent
    data object AddCategoryClicked : LibraryEvent
    data class EditCategoryClicked(val category: Category) : LibraryEvent
}
