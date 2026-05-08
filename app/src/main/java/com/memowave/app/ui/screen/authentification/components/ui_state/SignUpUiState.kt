package com.memowave.app.ui.screen.authentification.components.ui_state

/**
 * UI state for the sign-up screen.
 */
data class SignUpUiState(
    val form: SignUpFormState = SignUpFormState(),
    val isLoading: Boolean = false,
)
