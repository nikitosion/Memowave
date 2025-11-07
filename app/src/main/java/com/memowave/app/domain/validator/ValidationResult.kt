package com.memowave.app.domain.validator

/**
 * Represents the result of a validation operation.
 *
 * @property errorMessage The error message if validation failed, or null if valid.
 */
sealed class ValidationResult(open val errorMessage: String? = null) {
    /**
     * Indicates a valid result.
     */
    object Valid : ValidationResult()

    /**
     * Indicates an invalid result with an error message.
     *
     * @property errorMessage The reason why validation failed.
     */
    data class Invalid(override val errorMessage: String) : ValidationResult(errorMessage)

    val isValid: Boolean
        get() = this is Valid
}
