package com.memowave.app.ui.screen.library

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memowave.app.core.media.UriImageReader
import com.memowave.app.domain.model.Category
import com.memowave.app.domain.model.Word
import com.memowave.app.domain.usecase.category.AddCategoryUseCase
import com.memowave.app.domain.usecase.category.DeleteCategoryUseCase
import com.memowave.app.domain.usecase.category.GetCategoriesUseCase
import com.memowave.app.domain.usecase.category.GetCategoryUseCase
import com.memowave.app.domain.usecase.category.UpdateCategoryUseCase
import com.memowave.app.domain.usecase.media.DeleteImageUseCase
import com.memowave.app.domain.usecase.media.UploadImageUseCase
import com.memowave.app.domain.usecase.word.AddWordUseCase
import com.memowave.app.domain.usecase.word.DeleteWordUseCase
import com.memowave.app.domain.usecase.word.GetWordsUseCase
import com.memowave.app.domain.usecase.word.UpdateWordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val getWordsUseCase: GetWordsUseCase,
    private val addWordUseCase: AddWordUseCase,
    private val updateWordUseCase: UpdateWordUseCase,
    private val deleteWordUseCase: DeleteWordUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val addCategoryUseCase: AddCategoryUseCase,
    private val updateCategoryUseCase: UpdateCategoryUseCase,
    private val getCategoryUseCase: GetCategoryUseCase,
    private val deleteCategoryUseCase: DeleteCategoryUseCase,
    private val uploadImageUseCase: UploadImageUseCase,
    private val deleteImageUseCase: DeleteImageUseCase,
    private val uriImageReader: UriImageReader
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

            is LibraryEvent.AddWordClicked -> {
                _uiState.value = _uiState.value.copy(
                    isWordDialogOpen = true,
                    editingWord = null
                )
            }

            is LibraryEvent.EditWordClicked -> {
                _uiState.value = _uiState.value.copy(
                    isWordDialogOpen = true,
                    editingWord = event.word
                )
            }

            is LibraryEvent.DeleteWordClicked -> deleteWord(event.wordId)

            is LibraryEvent.DismissWordDialog -> {
                _uiState.value = _uiState.value.copy(
                    isWordDialogOpen = false,
                    editingWord = null
                )
            }

            is LibraryEvent.SaveWord -> saveWord(event)

            is LibraryEvent.AddCategoryClicked -> {
                _uiState.value = _uiState.value.copy(
                    isCategoryDialogOpen = true,
                    editingCategory = null
                )
            }

            is LibraryEvent.EditCategoryClicked -> {
                _uiState.value = _uiState.value.copy(
                    isCategoryDialogOpen = true,
                    editingCategory = event.category
                )
            }

            is LibraryEvent.DeleteCategoryClicked -> deleteCategory(event.categoryId)

            is LibraryEvent.DismissCategoryDialog -> {
                _uiState.value = _uiState.value.copy(
                    isCategoryDialogOpen = false,
                    editingCategory = null
                )
            }

            is LibraryEvent.SaveCategory -> saveCategory(event)
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

    private fun saveWord(event: LibraryEvent.SaveWord) {
        viewModelScope.launch {
            val currentEditing = _uiState.value.editingWord
            val previousImage = currentEditing?.imageUrl
            val baseWord = currentEditing?.copy(
                original = event.original.trim(),
                translation = event.translation.trim(),
                categoryId = event.categoryId,
                examples = event.examples,
                note = event.note?.takeIf { it.isNotBlank() },
                imageUrl = event.imageFileName
            ) ?: Word(
                original = event.original.trim(),
                translation = event.translation.trim(),
                categoryId = event.categoryId,
                examples = event.examples,
                note = event.note?.takeIf { it.isNotBlank() },
                imageUrl = event.imageFileName
            )

            val result = if (currentEditing == null) {
                addWordUseCase(baseWord)
            } else {
                updateWordUseCase(baseWord)
            }

            result.onSuccess { _ ->
                if (previousImage != null && previousImage != event.imageFileName) {
                    deleteImageBestEffort(previousImage)
                }
                onEvent(LibraryEvent.DismissWordDialog)
                loadData()
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    errorMessage = error.message ?: "Failed to save word"
                )
            }
        }
    }

    /**
     * Reads the picked URI, uploads it, and returns the resulting fileName.
     * Used by the dialog's image picker — kept as a suspend pass-through
     * so the picker stays oblivious to Hilt and use cases.
     */
    suspend fun uploadPickedImage(uri: Uri): Result<String> {
        return uriImageReader.read(uri)
            .mapCatching { picked ->
                uploadImageUseCase(picked.bytes, picked.mimeType).getOrThrow()
            }
    }

    fun discardPendingImage(fileName: String) {
        viewModelScope.launch { deleteImageBestEffort(fileName) }
    }

    private suspend fun deleteImageBestEffort(fileName: String) {
        deleteImageUseCase(fileName).onFailure {
            Timber.w(it, "Best-effort image delete failed for $fileName")
        }
    }

    private fun deleteWord(wordId: Long) {
        viewModelScope.launch {
            val result = deleteWordUseCase(wordId)
            result.onSuccess {
                loadData()
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    errorMessage = error.message ?: "Failed to delete word"
                )
            }
        }
    }

    private fun saveCategory(event: LibraryEvent.SaveCategory) {
        viewModelScope.launch {
            val currentEditing = _uiState.value.editingCategory
            val baseCategory = currentEditing?.copy(
                name = event.name.trim(),
                description = event.description?.takeIf { it.isNotBlank() },
                color = event.colorHex
            ) ?: Category(
                name = event.name.trim(),
                description = event.description?.takeIf { it.isNotBlank() },
                color = event.colorHex
            )

            val result = if (currentEditing == null) {
                addCategoryUseCase(baseCategory)
            } else {
                updateCategoryUseCase(baseCategory)
            }

            result.onSuccess {
                onEvent(LibraryEvent.DismissCategoryDialog)
                loadData()
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    errorMessage = error.message ?: "Failed to save category"
                )
            }
        }
    }

    private fun deleteCategory(categoryId: Long) {
        viewModelScope.launch {
            // For MVP simply prevent deletion if there are still words with this category
            val hasWordsWithCategory = _uiState.value.words.any { it.categoryId == categoryId }
            if (hasWordsWithCategory) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Удалите или перенесите слова из категории перед удалением"
                )
                return@launch
            }

            val result = deleteCategoryUseCase(categoryId)
            result.onSuccess {
                loadData()
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    errorMessage = error.message ?: "Failed to delete category"
                )
            }
        }
    }
}

