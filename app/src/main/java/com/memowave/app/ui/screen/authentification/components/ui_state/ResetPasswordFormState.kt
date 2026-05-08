package com.memowave.app.ui.screen.authentification.components.ui_state

/**
 * State for the reset-password form (new password + confirmation step).
 */
data class ResetPasswordFormState(
    val newPassword: String = "",
    val isNewPasswordValid: Boolean = false,
    val repeatedNewPassword: String = "",
    val isRepeatedNewPasswordValid: Boolean = false,
    val repeatedNewPasswordError: String? = null,
    val passwordValidationState: PasswordValidationState = PasswordValidationState(),
)
