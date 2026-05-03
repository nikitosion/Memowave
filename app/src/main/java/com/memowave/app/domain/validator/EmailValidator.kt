package com.memowave.app.domain.validator

import android.util.Patterns
import javax.inject.Inject

/**
 * Validates email addresses using Android's [Patterns.EMAIL_ADDRESS].
 *
 * Catches the vast majority of typos (missing `@`, missing TLD, spaces, Cyrillic chars).
 * Does NOT verify the address actually exists — that's the server's job after sending
 * a verification email.
 */
class EmailValidator @Inject constructor() {

    /**
     * Checks if the provided email is valid.
     *
     * @param email The email string to validate.
     * @return [ValidationResult] indicating if the email is valid or the error reason.
     */
    fun validate(email: String): ValidationResult = when {
        email.isEmpty() -> ValidationResult.Invalid("Email не может быть пустым")
        !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
            ValidationResult.Invalid("Некорректный формат email")
        else -> ValidationResult.Valid
    }
}
