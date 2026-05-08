package com.memowave.app.domain.validator

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Unit tests for [EmailValidator].
 *
 * Uses Robolectric because [android.util.Patterns.EMAIL_ADDRESS] is an Android-framework
 * resource that is unavailable in plain JVM tests.
 */
@RunWith(RobolectricTestRunner::class)
class EmailValidatorTest {

    private val validator = EmailValidator()

    @Test
    fun `empty email is invalid`() {
        val result = validator.validate("")

        assertTrue(result is ValidationResult.Invalid)
        assertEquals("Email не может быть пустым", (result as ValidationResult.Invalid).errorMessage)
    }

    @Test
    fun `well-formed email is valid`() {
        val result = validator.validate("user@example.com")
        assertTrue(result is ValidationResult.Valid)
    }

    @Test
    fun `email with plus addressing is valid`() {
        val result = validator.validate("user+tag@example.com")
        assertTrue(result is ValidationResult.Valid)
    }

    @Test
    fun `email with subdomain is valid`() {
        val result = validator.validate("user@mail.example.com")
        assertTrue(result is ValidationResult.Valid)
    }

    @Test
    fun `email without at sign is invalid`() {
        val result = validator.validate("userexample.com")
        assertTrue(result is ValidationResult.Invalid)
    }

    @Test
    fun `email without domain is invalid`() {
        val result = validator.validate("user@")
        assertTrue(result is ValidationResult.Invalid)
    }

    @Test
    fun `email without local part is invalid`() {
        val result = validator.validate("@example.com")
        assertTrue(result is ValidationResult.Invalid)
    }

    @Test
    fun `email with space is invalid`() {
        val result = validator.validate("us er@example.com")
        assertTrue(result is ValidationResult.Invalid)
    }

    @Test
    fun `email with cyrillic chars is invalid`() {
        val result = validator.validate("юзер@example.com")
        assertTrue(result is ValidationResult.Invalid)
    }

    @Test
    fun `email with multiple at signs is invalid`() {
        val result = validator.validate("a@b@c.com")
        assertTrue(result is ValidationResult.Invalid)
    }

    @Test
    fun `email without TLD is invalid`() {
        val result = validator.validate("user@host")
        assertTrue(result is ValidationResult.Invalid)
    }
}
