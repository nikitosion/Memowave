package com.memowave.app.domain.algorithm

import com.memowave.app.domain.model.settings.AppSettings
import com.memowave.app.domain.model.settings.ThemeMode
import com.memowave.app.domain.repository.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertSame
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FSRSFactoryTest {

    @Test
    fun `factory rebuilds FSRS when relevant settings change`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val scope = TestScope(dispatcher)
        val fakeRepo = FakeSettingsRepository(AppSettings())
        val factory = FSRSFactory(fakeRepo, scope)
        advanceUntilIdle()

        val initial = factory.current()
        // Change retention — factory should produce a new FSRS instance
        fakeRepo.emit(AppSettings(fsrsRequestRetention = 0.90))
        advanceUntilIdle()

        assertNotSame(
            "Settings change must trigger a rebuild — current() should return a new instance",
            initial,
            factory.current()
        )
    }

    @Test
    fun `irrelevant setting changes do not rebuild FSRS`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val scope = TestScope(dispatcher)
        val fakeRepo = FakeSettingsRepository(AppSettings())
        val factory = FSRSFactory(fakeRepo, scope)
        advanceUntilIdle()

        val initial = factory.current()
        // Theme is unrelated to FSRS — must not trigger rebuild
        fakeRepo.emit(AppSettings(themeMode = ThemeMode.DARK))
        advanceUntilIdle()

        assertSame(
            "Theme change should not rebuild FSRS",
            initial,
            factory.current()
        )
    }

    private class FakeSettingsRepository(initial: AppSettings) : SettingsRepository {
        private val state = MutableStateFlow(initial)

        fun emit(value: AppSettings) {
            state.value = value
        }

        override fun getSettings(): Flow<AppSettings> = state

        // Unused setters for these tests — left as no-ops
        override suspend fun setThemeMode(mode: ThemeMode) {}
        override suspend fun setAppLanguage(language: String?) {}
        override suspend fun setReduceMotion(enabled: Boolean) {}
        override suspend fun setWeeklyGoal(words: Int) {}
        override suspend fun setGoalDaysOfWeek(days: Set<Int>) {}
        override suspend fun setReminderTime(hour: Int, minute: Int) {}
        override suspend fun setFsrsRequestRetention(value: Double) {}
        override suspend fun setFsrsMaximumInterval(days: Int) {}
        override suspend fun setFsrsEasyBonus(value: Double) {}
        override suspend fun setFsrsHardPenalty(value: Double) {}
        override suspend fun resetFsrsToDefaults() {}
        override suspend fun setNotificationsEnabled(enabled: Boolean) {}
        override suspend fun setStudyRemindersEnabled(enabled: Boolean) {}
        override suspend fun setStreakRiskEnabled(enabled: Boolean) {}
        override suspend fun setMilestonesEnabled(enabled: Boolean) {}
        override suspend fun setQuietHoursEnabled(enabled: Boolean) {}
        override suspend fun setQuietHours(
            startHour: Int,
            startMinute: Int,
            endHour: Int,
            endMinute: Int
        ) {}
    }
}
