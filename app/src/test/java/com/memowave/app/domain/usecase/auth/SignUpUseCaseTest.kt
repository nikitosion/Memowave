package com.memowave.app.domain.usecase.auth

import com.memowave.app.core.auth.AuthStateManager
import com.memowave.app.domain.model.user.UserRegistration
import com.memowave.app.domain.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SignUpUseCaseTest {

    private val repository: AuthRepository = mockk()
    private val authStateManager: AuthStateManager = mockk(relaxed = true)
    private val useCase = SignUpUseCase(repository, authStateManager)

    @Test
    fun `repository receives a UserRegistration with the supplied fields`() = runTest {
        val captured = slot<UserRegistration>()
        coEvery { repository.register(capture(captured)) } returns Result.success(Unit)

        useCase(username = "Alice", email = "alice@x.com", password = "Pwd1!aaa")

        assertEquals("Alice", captured.captured.username)
        assertEquals("alice@x.com", captured.captured.email)
        assertEquals("Pwd1!aaa", captured.captured.password)
    }

    @Test
    fun `successful sign up marks user as authenticated`() = runTest {
        coEvery { repository.register(any()) } returns Result.success(Unit)

        val result = useCase("u", "e@x.com", "p")

        assertTrue(result.isSuccess)
        verify(exactly = 1) { authStateManager.setAuthenticated() }
    }

    @Test
    fun `failed sign up does not mark user as authenticated`() = runTest {
        val error = RuntimeException("server down")
        coEvery { repository.register(any()) } returns Result.failure(error)

        val result = useCase("u", "e@x.com", "p")

        assertTrue(result.isFailure)
        assertEquals(error, result.exceptionOrNull())
        verify(exactly = 0) { authStateManager.setAuthenticated() }
    }

    @Test
    fun `register is called exactly once`() = runTest {
        coEvery { repository.register(any()) } returns Result.success(Unit)

        useCase("u", "e@x.com", "p")

        coVerify(exactly = 1) { repository.register(any()) }
    }
}
