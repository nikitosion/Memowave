package com.memowave.app.domain.validator

import com.memowave.app.ui.screen.authentification.components.ui_state.PasswordValidationState
import javax.inject.Inject

/**
 * Validates password strength and matching logic.
 *
 * Provides methods for checking password length, complexity, and matching repeated passwords.
 */
class PasswordValidator @Inject constructor() {
    /**
     * Validates the password according to all rules (length, complexity, etc).
     *
     * @param password The password string to validate.
     * @return [PasswordValidationResult] with validation state and error info.
     */
    fun validate(password: String): PasswordValidationResult {
        if (password.isEmpty()) {
            return PasswordValidationResult(
                isValid = false,
                validationState = PasswordValidationState()
            )
        }

        val validationState = PasswordValidationState(
            hasMinLength = password.length >= MIN_LENGTH,
            hasLowercase = password.any { it.isLowerCase() },
            hasUppercase = password.any { it.isUpperCase() },
            hasDigit = password.any { it.isDigit() },
            hasSpecialChar = password.any { !it.isLetterOrDigit() }
        )

        return PasswordValidationResult(
            isValid = validationState.isAllValid,
            validationState = validationState
        )
    }

    /**
     * Checks if the password meets the minimum length requirement.
     *
     * @param password The password string to check.
     * @return true if the password is long enough, false otherwise.
     */
    fun validateLength(password: String): Boolean = password.length >= MIN_LENGTH

    /**
     * Checks if two passwords match.
     *
     * @param password The original password.
     * @param repeatedPassword The repeated password to compare.
     * @return [PasswordMatchResult] indicating if passwords match or the error reason.
     */
    fun validateMatch(password: String, repeatedPassword: String): ValidationResult {
        return when {
            repeatedPassword.isEmpty() -> ValidationResult.Invalid("Повторите пароль")
            password != repeatedPassword -> ValidationResult.Invalid("Пароли не совпадают")
            else -> ValidationResult.Valid
        }
    }

    companion object {
        private const val MIN_LENGTH = 8
    }
}

data class PasswordValidationResult(
    val isValid: Boolean,
    val validationState: PasswordValidationState
)
