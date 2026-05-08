package com.memowave.app.data.repository

import androidx.datastore.core.DataStore
import com.memowave.app.domain.model.settings.AppSettings
import com.memowave.app.domain.algorithm.FSRSConfig
import com.memowave.app.domain.model.settings.ThemeMode
import com.memowave.app.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

class SettingsRepositoryImpl(
    private val dataStore: DataStore<AppSettings>
) : SettingsRepository {

    override fun getSettings(): Flow<AppSettings> =
        dataStore.data.catch { emit(AppSettings()) }

    override suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.updateData { it.copy(themeMode = mode) }
    }

    override suspend fun setAppLanguage(language: String?) {
        dataStore.updateData { it.copy(appLanguage = language) }
    }

    override suspend fun setReduceMotion(enabled: Boolean) {
        dataStore.updateData { it.copy(reduceMotion = enabled) }
    }

    override suspend fun setWeeklyGoal(words: Int) {
        dataStore.updateData { it.copy(weeklyWordGoal = words.coerceIn(MIN_WEEKLY_GOAL, MAX_WEEKLY_GOAL)) }
    }

    override suspend fun setGoalDaysOfWeek(days: Set<Int>) {
        val sanitized = days.filter { it in 1..7 }.toSet()
        dataStore.updateData { it.copy(goalDaysOfWeek = sanitized) }
    }

    override suspend fun setReminderTime(hour: Int, minute: Int) {
        dataStore.updateData {
            it.copy(
                reminderHour = hour.coerceIn(0, 23),
                reminderMinute = minute.coerceIn(0, 59)
            )
        }
    }

    override suspend fun setFsrsRequestRetention(value: Double) {
        dataStore.updateData { it.copy(fsrsRequestRetention = value.coerceIn(0.85, 0.98)) }
    }

    override suspend fun setFsrsMaximumInterval(days: Int) {
        dataStore.updateData { it.copy(fsrsMaximumInterval = days.coerceIn(30, 365)) }
    }

    override suspend fun setFsrsEasyBonus(value: Double) {
        dataStore.updateData { it.copy(fsrsEasyBonus = value.coerceIn(1.0, 2.0)) }
    }

    override suspend fun setFsrsHardPenalty(value: Double) {
        dataStore.updateData { it.copy(fsrsHardPenalty = value.coerceIn(0.5, 1.0)) }
    }

    override suspend fun resetFsrsToDefaults() {
        dataStore.updateData {
            it.copy(
                fsrsRequestRetention = FSRSConfig.REQUEST_RETENTION,
                fsrsMaximumInterval = 365,
                fsrsEasyBonus = FSRSConfig.DEFAULT_PARAMS[16],
                fsrsHardPenalty = FSRSConfig.DEFAULT_PARAMS[15]
            )
        }
    }

    override suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.updateData { it.copy(notificationsEnabled = enabled) }
    }

    override suspend fun setStudyRemindersEnabled(enabled: Boolean) {
        dataStore.updateData { it.copy(studyRemindersEnabled = enabled) }
    }

    override suspend fun setStreakRiskEnabled(enabled: Boolean) {
        dataStore.updateData { it.copy(streakRiskEnabled = enabled) }
    }

    override suspend fun setMilestonesEnabled(enabled: Boolean) {
        dataStore.updateData { it.copy(milestonesEnabled = enabled) }
    }

    override suspend fun setQuietHoursEnabled(enabled: Boolean) {
        dataStore.updateData { it.copy(quietHoursEnabled = enabled) }
    }

    override suspend fun setQuietHours(startHour: Int, startMinute: Int, endHour: Int, endMinute: Int) {
        dataStore.updateData {
            it.copy(
                quietStartHour = startHour.coerceIn(0, 23),
                quietStartMinute = startMinute.coerceIn(0, 59),
                quietEndHour = endHour.coerceIn(0, 23),
                quietEndMinute = endMinute.coerceIn(0, 59),
            )
        }
    }

    override suspend fun setLastLearningMode(modeId: String) {
        dataStore.updateData { it.copy(lastLearningModeId = modeId) }
    }

    override suspend fun setLastFlashcardGameMode(mode: String) {
        dataStore.updateData { it.copy(lastFlashcardGameMode = mode) }
    }

    override suspend fun setStreakState(
        currentStreak: Int,
        longestStreak: Int,
        lastStreakDate: String?,
        wordsCompletedToday: Int,
        lastActivityDate: String?,
    ) {
        dataStore.updateData {
            it.copy(
                currentStreak = currentStreak.coerceAtLeast(0),
                longestStreak = longestStreak.coerceAtLeast(0),
                lastStreakDate = lastStreakDate,
                wordsCompletedToday = wordsCompletedToday.coerceAtLeast(0),
                lastActivityDate = lastActivityDate,
            )
        }
    }

    private companion object {
        const val MIN_WEEKLY_GOAL = 5
        const val MAX_WEEKLY_GOAL = 500
    }
}
