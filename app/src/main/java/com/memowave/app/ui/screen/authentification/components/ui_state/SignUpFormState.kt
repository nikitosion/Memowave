package com.memowave.app.ui.screen.authentification.components.ui_state

/**
 * State for the sign up form.
 *
 * Holds username, email, password, repeated password, and validation state.
 * @see AuthUiState
 */
data class SignUpFormState (
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val repeatedPassword: String = "",
    val isNameValid: Boolean = false,
    val isEmailValid: Boolean = false,
    val isPasswordValid: Boolean = false,
    val isRepeatedPasswordValid: Boolean = false,
    val nameError: String? = null,
    val emailError: String? = null,
    val repeatedPasswordError: String? = null,
    val passwordValidationState: PasswordValidationState = PasswordValidationState()
)