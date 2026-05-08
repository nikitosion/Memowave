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
    val fsrsMaximumInterval: Int = 365,
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

    val lastLearningModeId: String = "Каротчки",
    val lastFlashcardGameMode: String = "RECALL",
    val lastTranslationStrictness: String = "NORMAL", // STRICT / NORMAL / LENIENT
    val lastQuizDurationSeconds: Int = 60,            // 30 / 60 / 120

    // Streak state. Dates are ISO LocalDate strings ("YYYY-MM-DD") to keep
    // kotlinx.serialization happy without a custom serializer.
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastStreakDate: String? = null,
    val wordsCompletedToday: Int = 0,
    val lastActivityDate: String? = null,
)
