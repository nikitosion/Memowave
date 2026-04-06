package com.memowave.app.ui.screen.library

import com.memowave.app.domain.model.Category
import com.memowave.app.domain.model.Word

sealed interface LibraryEvent {
    data object Load : LibraryEvent
    data class SearchChanged(val query: String) : LibraryEvent
    data class CategorySelected(val categoryId: Long?) : LibraryEvent
    data object AddWordClicked : LibraryEvent
    data class EditWordClicked(val word: Word) : LibraryEvent
    data class DeleteWordClicked(val wordId: Long) : LibraryEvent
    data object DismissWordDialog : LibraryEvent
    data class SaveWord(
        val original: String,
        val translation: String,
        val categoryId: Long?,
        val examples: List<String>,
        val note: String?
    ) : LibraryEvent

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