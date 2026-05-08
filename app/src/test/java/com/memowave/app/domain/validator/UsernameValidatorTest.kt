package com.memowave.app.domain.validator

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UsernameValidatorTest {

    private val validator = UsernameValidator()

    @Test
    fun `empty username is invalid`() {
        val result = validator.validate("")

        assertTrue(result is ValidationResult.Invalid)
        assertEquals(
            "Имя не может быть пустым",
            (result as ValidationResult.Invalid).errorMessage
        )
    }

    @Test
    fun `single character is invalid`() {
        val result = validator.validate("a")
        assertTrue(result is ValidationResult.Invalid)
    }

    @Test
    fun `two characters is valid (boundary)`() {
        val result = validator.validate("ab")
        assertTrue(result is ValidationResult.Valid)
    }

    @Test
    fun `long username is valid`() {
        val result = validator.validate("Александр Пушкин")
        assertTrue(result is ValidationResult.Valid)
    }
}
