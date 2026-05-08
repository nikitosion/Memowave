package com.memowave.app.ui.screen.word_edit

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memowave.app.core.media.UriImageReader
import com.memowave.app.domain.model.Word
import com.memowave.app.domain.usecase.category.GetCategoriesUseCase
import com.memowave.app.domain.usecase.media.DeleteImageUseCase
import com.memowave.app.domain.usecase.media.UploadImageUseCase
import com.memowave.app.domain.usecase.word.AddWordUseCase
import com.memowave.app.domain.usecase.word.DeleteWordUseCase
import com.memowave.app.domain.usecase.word.GetWordUseCase
import com.memowave.app.domain.usecase.word.UpdateWordUseCase
import com.memowave.app.ui.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDateTime
import javax.inject.Inject

private const val SUCCESS_OVERLAY_MS = 900L
private const val ERROR_OVERLAY_MS = 1700L

@HiltViewModel
class WordEditViewModel @Inject constructor(
    private val getWordUseCase: GetWordUseCase,
    private val addWordUseCase: AddWordUseCase,
    private val updateWordUseCase: UpdateWordUseCase,
    private val deleteWordUseCase: DeleteWordUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val uploadImageUseCase: UploadImageUseCase,
    private val deleteImageUseCase: DeleteImageUseCase,
    private val uriImageReader: UriImageReader,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val wordId: Long? = savedStateHandle.get<Long>(Screen.WordEdit.WORD_ID_ARG)

    private val _uiState = MutableStateFlow(WordEditUiState(isCreate = wordId == null))
    val uiState: StateFlow<WordEditUiState> = _uiState.asStateFlow()

    init { onEvent(WordEditEvent.Load) }

    fun onEvent(event: WordEditEvent) {
        when (event) {
            WordEditEvent.Load -> load()
            is WordEditEvent.OriginalChanged ->
                _uiState.value = _uiState.value.copy(original = event.value)
            is WordEditEvent.TranslationChanged ->
                _uiState.value = _uiState.value.copy(translation = event.value)
            is WordEditEvent.ExampleChanged ->
                _uiState.value = _uiState.value.copy(example = event.value)
            is WordEditEvent.CategorySelected ->
                _uiState.value = _uiState.value.copy(selectedCategoryId = event.categoryId)
            is WordEditEvent.ImageChanged -> applyNewImage(event.fileName)

            WordEditEvent.Save -> save()

            WordEditEvent.DeleteRequested ->
                _uiState.value = _uiState.value.copy(showDeleteConfirm = true)
            WordEditEvent.DeleteCancelled ->
                _uiState.value = _uiState.value.copy(showDeleteConfirm = false)
            WordEditEvent.DeleteConfirmed -> delete()

            WordEditEvent.CancelRequested -> cancelRequested()
            WordEditEvent.CancelDismissed ->
                _uiState.value = _uiState.value.copy(showDiscardConfirm = false)
            WordEditEvent.CancelConfirmed ->
                _uiState.value = _uiState.value.copy(
                    showDiscardConfirm = false,
                    isFinished = true
                )

            WordEditEvent.ResetProgressRequested ->
                _uiState.value = _uiState.value.copy(progressResetRequested = true)
            WordEditEvent.UndoResetProgress ->
                _uiState.value = _uiState.value.copy(progressResetRequested = false)
        }
    }

    private fun load() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val categories = getCategoriesUseCase().getOrElse { emptyList() }

            if (wordId == null) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isCreate = true,
                    categories = categories
                )
                return@launch
            }

            val wordResult = getWordUseCase(wordId)
            val word = wordResult.getOrNull()
            if (word == null) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isCreate = true,
                    categories = categories,
                    operation = OperationStatus(
                        kind = OperationKind.Save,
                        phase = OperationPhase.Error,
                        errorMessage = wordResult.exceptionOrNull()?.message
                    )
                )
                return@launch
            }

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                isCreate = false,
                categories = categories,
                initialWord = word,
                original = word.original,
                translation = word.translation,
                example = word.examples.firstOrNull().orEmpty(),
                selectedCategoryId = word.categoryId,
                imageFileName = word.imageUrl
            )
        }
    }

    private fun applyNewImage(newFileName: String?) {
        val state = _uiState.value
        val previous = state.imageFileName
        val initialImage = state.initialWord?.imageUrl
        _uiState.value = state.copy(imageFileName = newFileName)
        if (previous != null && previous != newFileName && previous != initialImage) {
            viewModelScope.launch { deleteImageBestEffort(previous) }
        }
    }

    private fun cancelRequested() {
        if (_uiState.value.hasUnsavedChanges()) {
            _uiState.value = _uiState.value.copy(showDiscardConfirm = true)
        } else {
            _uiState.value = _uiState.value.copy(isFinished = true)
        }
    }

    private fun save() {
        val snapshot = _uiState.value
        if (!snapshot.canSave) return

        viewModelScope.launch {
            _uiState.value = snapshot.copy(
                operation = OperationStatus(OperationKind.Save, OperationPhase.InProgress)
            )

            val initial = snapshot.initialWord
            val previousImage = initial?.imageUrl
            val example = snapshot.example.trim()
            val now = System.currentTimeMillis()

            val baseWord = initial?.copy(
                original = snapshot.original.trim(),
                translation = snapshot.translation.trim(),
                categoryId = snapshot.selectedCategoryId,
                examples = if (example.isBlank()) emptyList() else listOf(example),
                imageUrl = snapshot.imageFileName,
                updatedAt = now
            ) ?: Word(
                original = snapshot.original.trim(),
                translation = snapshot.translation.trim(),
                categoryId = snapshot.selectedCategoryId,
                examples = if (example.isBlank()) emptyList() else listOf(example),
                imageUrl = snapshot.imageFileName,
                createdAt = now,
                updatedAt = now
            )

            val finalWord = if (snapshot.progressResetRequested && initial != null) {
                baseWord.copy(
                    stability = 2.5,
                    difficulty = 2.5,
                    interval = 0,
                    dueDate = LocalDateTime.now(),
                    reviewCount = 0,
                    lastReview = null,
                    phase = 0
                )
            } else baseWord

            val result = if (initial == null) addWordUseCase(finalWord)
            else updateWordUseCase(finalWord)

            result.onSuccess {
                if (previousImage != null && previousImage != snapshot.imageFileName) {
                    deleteImageBestEffort(previousImage)
                }
                _uiState.value = _uiState.value.copy(
                    operation = OperationStatus(OperationKind.Save, OperationPhase.Success)
                )
                delay(SUCCESS_OVERLAY_MS)
                _uiState.value = _uiState.value.copy(isFinished = true)
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    operation = OperationStatus(
                        kind = OperationKind.Save,
                        phase = OperationPhase.Error,
                        errorMessage = error.message
                    )
                )
                delay(ERROR_OVERLAY_MS)
                _uiState.value = _uiState.value.copy(operation = null)
            }
        }
    }

    private fun delete() {
        val id = wordId ?: return
        val initial = _uiState.value.initialWord

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                showDeleteConfirm = false,
                operation = OperationStatus(OperationKind.Delete, OperationPhase.InProgress)
            )
            val result = deleteWordUseCase(id)
            result.onSuccess {
                val image = initial?.imageUrl
                if (image != null) deleteImageBestEffort(image)
                _uiState.value = _uiState.value.copy(
                    operation = OperationStatus(OperationKind.Delete, OperationPhase.Success)
                )
                delay(SUCCESS_OVERLAY_MS)
                _uiState.value = _uiState.value.copy(isFinished = true)
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    operation = OperationStatus(
                        kind = OperationKind.Delete,
                        phase = OperationPhase.Error,
                        errorMessage = error.message
                    )
                )
                delay(ERROR_OVERLAY_MS)
                _uiState.value = _uiState.value.copy(operation = null)
            }
        }
    }

    suspend fun uploadPickedImage(uri: Uri): Result<String> =
        uriImageReader.read(uri).mapCatching { picked ->
            uploadImageUseCase(picked.bytes, picked.mimeType).getOrThrow()
        }

    private suspend fun deleteImageBestEffort(fileName: String) {
        deleteImageUseCase(fileName).onFailure {
            Timber.w(it, "Best-effort image delete failed for $fileName")
        }
    }
}
