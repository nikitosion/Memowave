package com.memowave.app.domain.usecase.settings

import com.memowave.app.domain.repository.SettingsRepository
import io.mockk.coVerify
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class UpdateGoalDaysUseCaseTest {

    private val repository: SettingsRepository = mockk(relaxed = true)
    private val useCase = UpdateGoalDaysUseCase(repository)

    @Test
    fun `empty set is silently ignored to avoid leaving user with no scheduled days`() = runTest {
        useCase(emptySet())

        coVerify(exactly = 0) { repository.setGoalDaysOfWeek(any()) }
    }

    @Test
    fun `non-empty set is forwarded to repository`() = runTest {
        useCase(setOf(1, 3, 5))

        coVerify(exactly = 1) { repository.setGoalDaysOfWeek(setOf(1, 3, 5)) }
    }

    @Test
    fun `single day is allowed`() = runTest {
        useCase(setOf(7))

        coVerify(exactly = 1) { repository.setGoalDaysOfWeek(setOf(7)) }
    }
}
