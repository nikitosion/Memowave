package com.memowave.app.ui.screen.authentification.components.ui_state

/**
 * Represents the validation state of a password according to various rules.
 *
 * Each property indicates if a specific rule is satisfied.
 * [isAllValid] is true if all rules are satisfied.
 */
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
