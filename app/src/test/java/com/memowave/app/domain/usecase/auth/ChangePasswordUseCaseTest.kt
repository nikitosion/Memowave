package com.memowave.app.domain.usecase.auth

import com.memowave.app.domain.repository.AuthRepository
import com.memowave.app.domain.validator.PasswordValidationResult
import com.memowave.app.domain.validator.PasswordValidator
import com.memowave.app.ui.screen.authentification.components.ui_state.PasswordValidationState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class ChangePasswordUseCaseTest {

    private val repository: AuthRepository = mockk()
    private val passwordValidator: PasswordValidator = mockk()
    private val useCase = ChangePasswordUseCase(repository, passwordValidator)

    private val validState = PasswordValidationState(
        hasMinLength = true,
        hasLowercase = true,
        hasUppercase = true,
        hasDigit = true,
        hasSpecialChar = true
    )

    @Test
    fun `same current and new password returns SamePassword failure without calling repo`() = runTest {
        val result = useCase("Same1!aa", "Same1!aa")

        assertTrue(result.isFailure)
        assertSame(ChangePasswordError.SamePassword, result.exceptionOrNull())
        coVerify(exactly = 0) { repository.changePassword(any(), any()) }
    }

    @Test
    fun `weak new password returns InvalidPassword without calling repo`() = runTest {
        every { passwordValidator.validate("weak") } returns PasswordValidationResult(
            isValid = false,
            validationState = PasswordValidationState(hasMinLength = false)
        )

        val result = useCase("Strong1!", "weak")

        assertTrue(result.isFailure)
        assertSame(ChangePasswordError.InvalidPassword, result.exceptionOrNull())
        coVerify(exactly = 0) { repository.changePassword(any(), any()) }
    }

    @Test
    fun `valid password delegates to repository and propagates success`() = runTest {
        every { passwordValidator.validate("NewStr0ng!") } returns PasswordValidationResult(
            isValid = true,
            validationState = validState
        )
        coEvery {
            repository.changePassword("OldStr0ng!", "NewStr0ng!")
        } returns Result.success(Unit)

        val result = useCase("OldStr0ng!", "NewStr0ng!")

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { repository.changePassword("OldStr0ng!", "NewStr0ng!") }
    }

    @Test
    fun `repository failure is propagated unchanged`() = runTest {
        every { passwordValidator.validate(any()) } returns PasswordValidationResult(
            isValid = true,
            validationState = validState
        )
        val error = ChangePasswordError.WrongCurrentPassword
        coEvery { repository.changePassword(any(), any()) } returns Result.failure(error)

        val result = useCase("OldStr0ng!", "NewStr0ng!")

        assertTrue(result.isFailure)
        assertEquals(error, result.exceptionOrNull())
    }
}
