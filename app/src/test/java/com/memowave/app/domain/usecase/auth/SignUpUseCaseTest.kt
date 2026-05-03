package com.memowave.app.domain.usecase.auth

import com.memowave.app.domain.model.user.UserRegistration
import com.memowave.app.domain.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SignUpUseCaseTest {

    private val repository: AuthRepository = mockk()
    private val useCase = SignUpUseCase(repository)

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
    fun `successful repository result is propagated`() = runTest {
        coEvery { repository.register(any()) } returns Result.success(Unit)

        val result = useCase("u", "e@x.com", "p")

        assertTrue(result.isSuccess)
    }

    @Test
    fun `failed repository result is propagated`() = runTest {
        val error = RuntimeException("server down")
        coEvery { repository.register(any()) } returns Result.failure(error)

        val result = useCase("u", "e@x.com", "p")

        assertTrue(result.isFailure)
        assertEquals(error, result.exceptionOrNull())
    }

    @Test
    fun `register is called exactly once`() = runTest {
        coEvery { repository.register(any()) } returns Result.success(Unit)

        useCase("u", "e@x.com", "p")

        coVerify(exactly = 1) { repository.register(any()) }
    }
}
