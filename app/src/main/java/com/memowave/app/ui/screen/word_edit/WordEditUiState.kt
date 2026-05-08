package com.memowave.app.ui.screen.word_edit

import com.memowave.app.domain.model.Category
import com.memowave.app.domain.model.Word

data class WordEditUiState(
    val isCreate: Boolean = true,
    val isLoading: Boolean = false,
    val operation: OperationStatus? = null,
    val showDeleteConfirm: Boolean = false,
    val showDiscardConfirm: Boolean = false,
    val isFinished: Boolean = false,

    val original: String = "",
    val translation: String = "",
    val example: String = "",
    val selectedCategoryId: Long? = null,
    val imageFileName: String? = null,
    val categories: List<Category> = emptyList(),
    val progressResetRequested: Boolean = false,

    val initialWord: Word? = null
) {
    val canSave: Boolean
        get() = original.isNotBlank() && translation.isNotBlank() && operation == null

    val isBusy: Boolean
        get() = operation?.phase == OperationPhase.InProgress

    fun hasUnsavedChanges(): Boolean {
        val initial = initialWord
        val o = original.trim()
        val t = translation.trim()
        val e = example.trim()
        if (initial == null) {
            return o.isNotEmpty() ||
                t.isNotEmpty() ||
                e.isNotEmpty() ||
                selectedCategoryId != null ||
                imageFileName != null
        }
        val initialExample = initial.examples.firstOrNull().orEmpty()
        return o != initial.original ||
            t != initial.translation ||
            e != initialExample ||
            selectedCategoryId != initial.categoryId ||
            imageFileName != initial.imageUrl ||
            progressResetRequested
    }
}

data class OperationStatus(
    val kind: OperationKind,
    val phase: OperationPhase,
    val errorMessage: String? = null
)

enum class OperationKind { Save, Delete }
enum class OperationPhase { InProgress, Success, Error }
