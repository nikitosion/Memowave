package com.memowave.app.domain.usecase.profile

import com.memowave.app.domain.model.user.User
import com.memowave.app.domain.repository.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UpdateUsernameUseCaseTest {

    private val repository: UserRepository = mockk()
    private val useCase = UpdateUsernameUseCase(repository)

    @Test
    fun `single character username is rejected as too short`() = runTest {
        val result = useCase("a")

        assertTrue(result.isFailure)
        assertEquals(
            UpdateUsernameUseCase.ERR_TOO_SHORT,
            result.exceptionOrNull()?.message
        )
        coVerify(exactly = 0) { repository.updateUsername(any()) }
    }

    @Test
    fun `whitespace-only username is rejected after trim`() = runTest {
        val result = useCase("   ")

        assertTrue(result.isFailure)
        assertEquals(UpdateUsernameUseCase.ERR_TOO_SHORT, result.exceptionOrNull()?.message)
    }

    @Test
    fun `username over 32 chars is rejected`() = runTest {
        val tooLong = "x".repeat(33)
        val result = useCase(tooLong)

        assertTrue(result.isFailure)
        assertEquals(UpdateUsernameUseCase.ERR_TOO_LONG, result.exceptionOrNull()?.message)
        coVerify(exactly = 0) { repository.updateUsername(any()) }
    }

    @Test
    fun `valid username is trimmed and forwarded to repository`() = runTest {
        coEvery { repository.updateUsername("nikita") } returns Result.success(
            User(username = "nikita")
        )

        val result = useCase("  nikita  ")

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { repository.updateUsername("nikita") }
    }
}
