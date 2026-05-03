package com.memowave.app.ui.screen.authentification.components.ui_state

/**
 * UI state for the login screen.
 */
data class LoginUiState(
    val form: LoginFormState = LoginFormState(),
    val isLoading: Boolean = false,
)
