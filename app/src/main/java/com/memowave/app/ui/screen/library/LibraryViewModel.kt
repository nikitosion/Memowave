package com.memowave.app.ui.screen.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memowave.app.domain.usecase.category.GetCategoriesUseCase
import com.memowave.app.domain.usecase.word.GetWordsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val getWordsUseCase: GetWordsUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    fun onEvent(event: LibraryEvent) {
        when (event) {
            is LibraryEvent.Load -> loadData()
            is LibraryEvent.SearchChanged -> {
                _uiState.value = _uiState.value.copy(searchQuery = event.query)
            }
            is LibraryEvent.CategorySelected -> {
                _uiState.value = _uiState.value.copy(selectedCategoryId = event.categoryId)
            }
            // Navigation events are handled in LibraryRoute, not here.
            is LibraryEvent.AddWordClicked,
            is LibraryEvent.EditWordClicked,
            is LibraryEvent.AddCategoryClicked,
            is LibraryEvent.EditCategoryClicked -> Unit
        }
    }

    private fun loadData() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val wordsResult = getWordsUseCase()
            val categoriesResult = getCategoriesUseCase()

            val words = wordsResult.getOrElse {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = it.message ?: "Failed to load words"
                )
                emptyList()
            }
            val categories = categoriesResult.getOrElse {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = it.message ?: "Failed to load categories"
                )
                emptyList()
            }

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                words = words,
                categories = categories
            )
        }
    }
}
