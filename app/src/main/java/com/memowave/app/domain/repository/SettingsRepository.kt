package com.memowave.app.domain.repository

import com.memowave.app.domain.model.settings.AppSettings
import com.memowave.app.domain.model.settings.ThemeMode
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getSettings(): Flow<AppSettings>

    suspend fun setThemeMode(mode: ThemeMode)
    suspend fun setAppLanguage(language: String?)
    suspend fun setReduceMotion(enabled: Boolean)

    suspend fun setWeeklyGoal(words: Int)
    suspend fun setGoalDaysOfWeek(days: Set<Int>)
    suspend fun setReminderTime(hour: Int, minute: Int)

    suspend fun setFsrsRequestRetention(value: Double)
    suspend fun setFsrsMaximumInterval(days: Int)
    suspend fun setFsrsEasyBonus(value: Double)
    suspend fun setFsrsHardPenalty(value: Double)
    suspend fun resetFsrsToDefaults()

    suspend fun setNotificationsEnabled(enabled: Boolean)
    suspend fun setStudyRemindersEnabled(enabled: Boolean)
    suspend fun setStreakRiskEnabled(enabled: Boolean)
    suspend fun setMilestonesEnabled(enabled: Boolean)
    suspend fun setQuietHoursEnabled(enabled: Boolean)
    suspend fun setQuietHours(startHour: Int, startMinute: Int, endHour: Int, endMinute: Int)

    suspend fun setLastLearningMode(modeId: String)
    suspend fun setLastFlashcardGameMode(mode: String)
    suspend fun setLastTranslationStrictness(strictness: String)
    suspend fun setLastQuizDurationSeconds(seconds: Int)
}
