package com.memowave.app.domain.validator

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PasswordValidatorTest {

    private val validator = PasswordValidator()

    // ----- validate(password) -----

    @Test
    fun `empty password is invalid with empty rule state`() {
        val result = validator.validate("")

        assertFalse(result.isValid)
        assertFalse(result.validationState.isAllValid)
    }

    @Test
    fun `password missing uppercase is invalid`() {
        val result = validator.validate("abcdefg1!")

        assertFalse(result.isValid)
        assertEquals(true, result.validationState.hasMinLength)
        assertEquals(true, result.validationState.hasLowercase)
        assertEquals(false, result.validationState.hasUppercase)
        assertEquals(true, result.validationState.hasDigit)
        assertEquals(true, result.validationState.hasSpecialChar)
    }

    @Test
    fun `password meeting all rules is valid`() {
        val result = validator.validate("Abcdefg1!")

        assertTrue(result.isValid)
        assertTrue(result.validationState.isAllValid)
    }

    @Test
    fun `seven character password fails minLength`() {
        val result = validator.validate("Abcd1!x")

        assertFalse(result.isValid)
        assertEquals(false, result.validationState.hasMinLength)
    }

    // ----- validateLength -----

    @Test
    fun `validateLength returns true for 8 chars`() {
        assertTrue(validator.validateLength("12345678"))
    }

    @Test
    fun `validateLength returns false for 7 chars`() {
        assertFalse(validator.validateLength("1234567"))
    }

    // ----- validateMatch -----

    @Test
    fun `matching passwords are valid`() {
        val result = validator.validateMatch("Abcdef1!", "Abcdef1!")
        assertTrue(result is ValidationResult.Valid)
    }

    @Test
    fun `empty repeated password says repeat`() {
        val result = validator.validateMatch("Abcdef1!", "")

        assertTrue(result is ValidationResult.Invalid)
        assertEquals("Повторите пароль", (result as ValidationResult.Invalid).errorMessage)
    }

    @Test
    fun `mismatched passwords are invalid`() {
        val result = validator.validateMatch("Abcdef1!", "Abcdef1?")

        assertTrue(result is ValidationResult.Invalid)
        assertEquals("Пароли не совпадают", (result as ValidationResult.Invalid).errorMessage)
    }
}
