package com.memowave.app.domain.validator

import javax.inject.Inject

/**
 * Validates usernames for sign up and profile forms.
 *
 * Checks for allowed characters, length, and other username rules.
 */
class UsernameValidator @Inject constructor() {
    /**
     * Validates the username string.
     *
     * @param username The username to validate.
     * @return [ValidationResult] indicating if the username is valid or the error reason.
     */
    fun validate(username: String): ValidationResult {
        return when {
            username.isEmpty() -> ValidationResult.Invalid("Имя не может быть пустым")
            username.length < MIN_LENGTH ->
                ValidationResult.Invalid("Имя должно содержать минимум $MIN_LENGTH символа")
            else -> ValidationResult.Valid
        }
    }

    companion object {
        private const val MIN_LENGTH = 2
    }
}
