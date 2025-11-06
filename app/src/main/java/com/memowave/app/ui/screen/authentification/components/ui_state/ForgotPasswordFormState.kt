package com.memowave.app.ui.screen.authentification.components.ui_state

data class ForgotPasswordFormState(
    val email: String = "",
    val isEmailValid: Boolean = false,
    val emailError: String? = null,
    val newPassword: String = "",
    val isNewPasswordValid: Boolean = false,
    val repeatedNewPassword: String = "",
    val isRepeatedNewPasswordValid: Boolean = false,
    val repeatedNewPasswordError: String? = null,
    val passwordValidationState: PasswordValidationState = PasswordValidationState()
)