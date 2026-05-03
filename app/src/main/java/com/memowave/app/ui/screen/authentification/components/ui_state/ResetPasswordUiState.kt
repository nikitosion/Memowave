package com.memowave.app.ui.screen.authentification.components.ui_state

/**
 * UI state for the reset-password (new password) screen.
 */
data class ResetPasswordUiState(
    val form: ResetPasswordFormState = ResetPasswordFormState(),
    val isLoading: Boolean = false,
)
