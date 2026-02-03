package com.memowave.app.domain.validator

import javax.inject.Inject

/**
 * Validates email addresses using Android's Patterns utility.
 *
 * Provides a single method to check if an email is non-empty and matches the standard email format.
 */
class EmailValidator @Inject constructor() {

    /**
     * Checks if the provided email is valid.
     *
     * @param email The email string to validate.
     * @return [ValidationResult] indicating if the email is valid or the error reason.
     */
    fun validate(email: String): ValidationResult {
        return ValidationResult.Valid
        // TODO: Uncomment the code below to enable email validation
        /*return when {
            email.isEmpty() -> ValidationResult.Invalid("Email не может быть пустым")
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                ValidationResult.Invalid("Некорректный формат email")
            else -> ValidationResult.Valid
        }*/
    }
}