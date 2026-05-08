package com.memowave.app.ui.screen.category_edit

import com.memowave.app.domain.model.Category
import com.memowave.app.domain.model.Word

data class CategoryEditUiState(
    val isCreate: Boolean = true,
    val isLoading: Boolean = false,
    val operation: OperationStatus? = null,
    val showDeleteConfirm: Boolean = false,
    val showDiscardConfirm: Boolean = false,
    val isFinished: Boolean = false,

    val name: String = "",
    val description: String = "",
    val color: String? = null,
    val iconName: String? = null,
    val isCustomColorOpen: Boolean = false,
    val customColorInput: String = "",

    val words: List<Word> = emptyList(),
    val initialCategory: Category? = null
) {
    val canSave: Boolean
        get() = name.isNotBlank() && operation == null

    val isBusy: Boolean
        get() = operation?.phase == OperationPhase.InProgress

    val canDelete: Boolean
        get() = !isCreate && words.isEmpty() && !isBusy

    fun hasUnsavedChanges(): Boolean {
        val initial = initialCategory
        val n = name.trim()
        val d = description.trim()
        if (initial == null) {
            return n.isNotEmpty() || d.isNotEmpty() || color != null || iconName != null
        }
        return n != initial.name ||
            d != initial.description.orEmpty() ||
            color != initial.color ||
            iconName != initial.iconName
    }
}

data class OperationStatus(
    val kind: OperationKind,
    val phase: OperationPhase,
    val errorMessage: String? = null
)

enum class OperationKind { Save, Delete }
enum class OperationPhase { InProgress, Success, Error }
