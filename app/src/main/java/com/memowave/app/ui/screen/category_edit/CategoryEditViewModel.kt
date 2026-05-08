package com.memowave.app.ui.screen.category_edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memowave.app.domain.model.Category
import com.memowave.app.domain.usecase.category.AddCategoryUseCase
import com.memowave.app.domain.usecase.category.DeleteCategoryUseCase
import com.memowave.app.domain.usecase.category.GetCategoryUseCase
import com.memowave.app.domain.usecase.category.UpdateCategoryUseCase
import com.memowave.app.domain.usecase.word.GetWordsByCategoryUseCase
import com.memowave.app.ui.common.category.CategoryPalette
import com.memowave.app.ui.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val SUCCESS_OVERLAY_MS = 900L
private const val ERROR_OVERLAY_MS = 1700L

@HiltViewModel
class CategoryEditViewModel @Inject constructor(
    private val getCategoryUseCase: GetCategoryUseCase,
    private val addCategoryUseCase: AddCategoryUseCase,
    private val updateCategoryUseCase: UpdateCategoryUseCase,
    private val deleteCategoryUseCase: DeleteCategoryUseCase,
    private val getWordsByCategoryUseCase: GetWordsByCategoryUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val categoryId: Long? = savedStateHandle.get<Long>(Screen.CategoryEdit.CATEGORY_ID_ARG)

    private val _uiState = MutableStateFlow(CategoryEditUiState(isCreate = categoryId == null))
    val uiState: StateFlow<CategoryEditUiState> = _uiState.asStateFlow()

    init { onEvent(CategoryEditEvent.Load) }

    fun onEvent(event: CategoryEditEvent) {
        when (event) {
            CategoryEditEvent.Load -> load()

            is CategoryEditEvent.NameChanged ->
                _uiState.value = _uiState.value.copy(name = event.value)
            is CategoryEditEvent.DescriptionChanged ->
                _uiState.value = _uiState.value.copy(description = event.value)
            is CategoryEditEvent.ColorSelected ->
                _uiState.value = _uiState.value.copy(
                    color = event.hex,
                    customColorInput = event.hex.orEmpty()
                )
            is CategoryEditEvent.IconSelected ->
                _uiState.value = _uiState.value.copy(iconName = event.name)
            is CategoryEditEvent.CustomColorChanged -> {
                val raw = event.value
                val color = if (CategoryPalette.isValidHex(raw)) {
                    CategoryPalette.normalizeHex(raw)
                } else null
                _uiState.value = _uiState.value.copy(
                    customColorInput = raw,
                    color = color ?: _uiState.value.color
                )
            }
            CategoryEditEvent.ToggleCustomColor ->
                _uiState.value = _uiState.value.copy(
                    isCustomColorOpen = !_uiState.value.isCustomColorOpen
                )

            CategoryEditEvent.Save -> save()

            CategoryEditEvent.DeleteRequested ->
                _uiState.value = _uiState.value.copy(showDeleteConfirm = true)
            CategoryEditEvent.DeleteCancelled ->
                _uiState.value = _uiState.value.copy(showDeleteConfirm = false)
            CategoryEditEvent.DeleteConfirmed -> delete()

            CategoryEditEvent.CancelRequested -> cancelRequested()
            CategoryEditEvent.CancelDismissed ->
                _uiState.value = _uiState.value.copy(showDiscardConfirm = false)
            CategoryEditEvent.CancelConfirmed ->
                _uiState.value = _uiState.value.copy(
                    showDiscardConfirm = false,
                    isFinished = true
                )
        }
    }

    private fun load() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            if (categoryId == null) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isCreate = true
                )
                return@launch
            }

            val categoryResult = getCategoryUseCase(categoryId)
            val category = categoryResult.getOrNull()
            if (category == null) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isCreate = true,
                    operation = OperationStatus(
                        kind = OperationKind.Save,
                        phase = OperationPhase.Error,
                        errorMessage = categoryResult.exceptionOrNull()?.message
                    )
                )
                return@launch
            }

            val words = getWordsByCategoryUseCase(categoryId).getOrElse { emptyList() }

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                isCreate = false,
                initialCategory = category,
                name = category.name,
                description = category.description.orEmpty(),
                color = category.color,
                iconName = category.iconName,
                customColorInput = category.color.orEmpty(),
                words = words
            )
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

            val initial = snapshot.initialCategory
            val now = System.currentTimeMillis()
            val baseCategory = initial?.copy(
                name = snapshot.name.trim(),
                description = snapshot.description.trim().ifEmpty { null },
                color = snapshot.color,
                iconName = snapshot.iconName,
                updatedAt = now
            ) ?: Category(
                name = snapshot.name.trim(),
                description = snapshot.description.trim().ifEmpty { null },
                color = snapshot.color,
                iconName = snapshot.iconName,
                createdAt = now,
                updatedAt = now
            )

            val result = if (initial == null) addCategoryUseCase(baseCategory)
            else updateCategoryUseCase(baseCategory)

            result.onSuccess {
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
        val id = categoryId ?: return
        if (_uiState.value.words.isNotEmpty()) {
            _uiState.value = _uiState.value.copy(
                showDeleteConfirm = false,
                operation = OperationStatus(
                    kind = OperationKind.Delete,
                    phase = OperationPhase.Error,
                    errorMessage = null
                )
            )
            viewModelScope.launch {
                delay(ERROR_OVERLAY_MS)
                _uiState.value = _uiState.value.copy(operation = null)
            }
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                showDeleteConfirm = false,
                operation = OperationStatus(OperationKind.Delete, OperationPhase.InProgress)
            )
            val result = deleteCategoryUseCase(id)
            result.onSuccess {
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
}
