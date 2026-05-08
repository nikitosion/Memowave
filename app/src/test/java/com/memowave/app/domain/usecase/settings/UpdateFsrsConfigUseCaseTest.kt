package com.memowave.app.domain.usecase.settings

import com.memowave.app.domain.repository.SettingsRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class UpdateFsrsConfigUseCaseTest {

    private val repository: SettingsRepository = mockk(relaxed = true)
    private val useCase = UpdateFsrsConfigUseCase(repository)

    @Test
    fun `null fields skip their corresponding setters`() = runTest {
        useCase(requestRetention = 0.92)

        coVerify(exactly = 1) { repository.setFsrsRequestRetention(0.92) }
        coVerify(exactly = 0) { repository.setFsrsMaximumInterval(any()) }
        coVerify(exactly = 0) { repository.setFsrsEasyBonus(any()) }
        coVerify(exactly = 0) { repository.setFsrsHardPenalty(any()) }
    }

    @Test
    fun `all four knobs can be updated in one call`() = runTest {
        useCase(
            requestRetention = 0.94,
            maximumInterval = 365,
            easyBonus = 1.5,
            hardPenalty = 0.7
        )

        coVerify(exactly = 1) { repository.setFsrsRequestRetention(0.94) }
        coVerify(exactly = 1) { repository.setFsrsMaximumInterval(365) }
        coVerify(exactly = 1) { repository.setFsrsEasyBonus(1.5) }
        coVerify(exactly = 1) { repository.setFsrsHardPenalty(0.7) }
    }

    @Test
    fun `no fields means no calls`() = runTest {
        useCase()

        coVerify(exactly = 0) { repository.setFsrsRequestRetention(any()) }
        coVerify(exactly = 0) { repository.setFsrsMaximumInterval(any()) }
        coVerify(exactly = 0) { repository.setFsrsEasyBonus(any()) }
        coVerify(exactly = 0) { repository.setFsrsHardPenalty(any()) }
    }
}
