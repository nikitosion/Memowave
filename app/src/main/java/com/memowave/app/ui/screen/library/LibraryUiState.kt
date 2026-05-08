package com.memowave.app.ui.screen.library

import com.memowave.app.domain.model.Category
import com.memowave.app.domain.model.Word

data class LibraryUiState(
    val isLoading: Boolean = false,
    val words: List<Word> = emptyList(),
    val categories: List<Category> = emptyList(),
    val selectedCategoryId: Long? = null,
    val searchQuery: String = "",
    val errorMessage: String? = null
)
