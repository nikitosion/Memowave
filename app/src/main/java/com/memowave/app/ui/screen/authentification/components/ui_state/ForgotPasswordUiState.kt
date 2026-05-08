package com.memowave.app.ui.screen.authentification.components.ui_state

/**
 * UI state for the forgot-password (email lookup) screen.
 */
data class ForgotPasswordUiState(
    val form: ForgotPasswordFormState = ForgotPasswordFormState(),
    val isLoading: Boolean = false,
)
