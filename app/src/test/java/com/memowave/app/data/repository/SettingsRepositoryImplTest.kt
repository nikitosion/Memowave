package com.memowave.app.data.repository

import androidx.datastore.core.DataStore
import com.memowave.app.domain.algorithm.FSRSConfig
import com.memowave.app.domain.model.settings.AppSettings
import com.memowave.app.domain.model.settings.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class SettingsRepositoryImplTest {

    private val store = FakeDataStore(AppSettings())
    private val repo = SettingsRepositoryImpl(store)

    @Test
    fun `theme mode is persisted via copy`() = runTest {
        repo.setThemeMode(ThemeMode.DARK)
        assertEquals(ThemeMode.DARK, repo.getSettings().first().themeMode)
    }

    @Test
    fun `weekly goal below minimum is clamped to 5`() = runTest {
        repo.setWeeklyGoal(0)
        assertEquals(5, repo.getSettings().first().weeklyWordGoal)
    }

    @Test
    fun `weekly goal above maximum is clamped to 500`() = runTest {
        repo.setWeeklyGoal(99999)
        assertEquals(500, repo.getSettings().first().weeklyWordGoal)
    }

    @Test
    fun `goal days outside ISO range are filtered`() = runTest {
        repo.setGoalDaysOfWeek(setOf(0, 1, 8, 5, -1, 7))

        // 0, 8, -1 are stripped; only 1, 5, 7 survive
        assertEquals(setOf(1, 5, 7), repo.getSettings().first().goalDaysOfWeek)
    }

    @Test
    fun `reminder time clamps hour and minute`() = runTest {
        repo.setReminderTime(hour = 25, minute = 90)
        val s = repo.getSettings().first()
        assertEquals(23, s.reminderHour)
        assertEquals(59, s.reminderMinute)
    }

    @Test
    fun `request retention is clamped to 0_85 to 0_98`() = runTest {
        repo.setFsrsRequestRetention(0.50)
        assertEquals(0.85, repo.getSettings().first().fsrsRequestRetention, 1e-6)

        repo.setFsrsRequestRetention(1.10)
        assertEquals(0.98, repo.getSettings().first().fsrsRequestRetention, 1e-6)
    }

    @Test
    fun `max interval is clamped to 30 to 36500`() = runTest {
        repo.setFsrsMaximumInterval(1)
        assertEquals(30, repo.getSettings().first().fsrsMaximumInterval)

        repo.setFsrsMaximumInterval(99999)
        assertEquals(36500, repo.getSettings().first().fsrsMaximumInterval)
    }

    @Test
    fun `reset fsrs to defaults restores config constants`() = runTest {
        repo.setFsrsRequestRetention(0.85)
        repo.setFsrsMaximumInterval(30)
        repo.setFsrsEasyBonus(1.0)
        repo.setFsrsHardPenalty(1.0)

        repo.resetFsrsToDefaults()

        val s = repo.getSettings().first()
        assertEquals(FSRSConfig.REQUEST_RETENTION, s.fsrsRequestRetention, 1e-6)
        assertEquals(36500, s.fsrsMaximumInterval)
        assertEquals(FSRSConfig.DEFAULT_PARAMS[16], s.fsrsEasyBonus, 1e-6)
        assertEquals(FSRSConfig.DEFAULT_PARAMS[15], s.fsrsHardPenalty, 1e-6)
    }

    @Test
    fun `quiet hours clamps both endpoints`() = runTest {
        repo.setQuietHours(startHour = -5, startMinute = 75, endHour = 30, endMinute = -1)
        val s = repo.getSettings().first()
        assertEquals(0, s.quietStartHour)
        assertEquals(59, s.quietStartMinute)
        assertEquals(23, s.quietEndHour)
        assertEquals(0, s.quietEndMinute)
    }
}

/**
 * In-memory fake [DataStore] backed by a [MutableStateFlow]. Sufficient for repository
 * tests — verifies the `copy { ... }`-mutation contract without disk IO.
 */
private class FakeDataStore<T>(initial: T) : DataStore<T> {
    private val state = MutableStateFlow(initial)
    override val data: Flow<T> = state

    override suspend fun updateData(transform: suspend (t: T) -> T): T {
        val next = transform(state.value)
        state.value = next
        return next
    }
}
