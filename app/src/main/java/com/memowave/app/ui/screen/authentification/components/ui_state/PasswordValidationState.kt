package com.memowave.app.ui.screen.authentification.components.ui_state

data class PasswordValidationState(
    val hasMinLength: Boolean? = null,
    val hasLowercase: Boolean? = null,
    val hasUppercase: Boolean? = null,
    val hasDigit: Boolean? = null,
    val hasSpecialChar: Boolean? = null,
) {
    val isAllValid: Boolean
        get() = hasMinLength == true && hasDigit == true && hasLowercase == true && hasUppercase == true && hasSpecialChar == true
}
