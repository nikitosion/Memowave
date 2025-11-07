package com.memowave.app.ui.screen.authentification.components.ui_state

/**
 * State for the login form.
 *
 * Holds email, password, and validation errors for the login screen.
 * @see AuthUiState
 */
data class LoginFormState(
    val email: String = "",
    val password: String = "",
    val isEmailValid: Boolean = false,
    val isPasswordValid: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
)
