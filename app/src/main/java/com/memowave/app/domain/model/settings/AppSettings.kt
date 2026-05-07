package com.memowave.app.domain.model.settings

import kotlinx.serialization.Serializable

@Serializable
data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val appLanguage: String? = null,
    val reduceMotion: Boolean = false,

    val weeklyWordGoal: Int = 50,
    val goalDaysOfWeek: Set<Int> = setOf(1, 2, 3, 4, 5),
    val reminderHour: Int = 19,
    val reminderMinute: Int = 0,

    val fsrsRequestRetention: Double = 0.95,
    val fsrsMaximumInterval: Int = 36500,
    val fsrsEasyBonus: Double = 1.8729,
    val fsrsHardPenalty: Double = 0.6014,

    val notificationsEnabled: Boolean = true,
    val studyRemindersEnabled: Boolean = true,
    val streakRiskEnabled: Boolean = false,
    val milestonesEnabled: Boolean = true,
    val quietHoursEnabled: Boolean = false,
    val quietStartHour: Int = 22,
    val quietStartMinute: Int = 0,
    val quietEndHour: Int = 8,
    val quietEndMinute: Int = 0,
)
