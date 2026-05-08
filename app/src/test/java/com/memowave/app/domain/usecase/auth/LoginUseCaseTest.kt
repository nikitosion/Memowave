package com.memowave.app.domain.usecase.auth

import com.memowave.app.core.auth.AuthStateManager
import com.memowave.app.domain.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LoginUseCaseTest {

    private val repository: AuthRepository = mockk()
    private val authStateManager: AuthStateManager = mockk(relaxed = true)
    private val useCase = LoginUseCase(repository, authStateManager)

    @Test
    fun `successful login marks user as authenticated`() = runTest {
        coEvery { repository.login("a@b.com", "pwd") } returns Result.success(Unit)

        val result = useCase("a@b.com", "pwd")

        assertTrue(result.isSuccess)
        verify(exactly = 1) { authStateManager.setAuthenticated() }
    }

    @Test
    fun `failed login does not mark user as authenticated`() = runTest {
        val error = RuntimeException("invalid credentials")
        coEvery { repository.login(any(), any()) } returns Result.failure(error)

        val result = useCase("a@b.com", "wrong")

        assertTrue(result.isFailure)
        assertEquals(error, result.exceptionOrNull())
        verify(exactly = 0) { authStateManager.setAuthenticated() }
    }

    @Test
    fun `repository is called with provided credentials`() = runTest {
        coEvery { repository.login(any(), any()) } returns Result.success(Unit)

        useCase("user@example.com", "secret")

        coVerify(exactly = 1) { repository.login("user@example.com", "secret") }
    }
}
