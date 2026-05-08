package com.memowave.app.ui.screen.settings.goals

data class GoalsUiState(
    val weeklyWordGoal: Int = 50,
    val goalDays: Set<Int> = setOf(1, 2, 3, 4, 5),
    val reminderHour: Int = 19,
    val reminderMinute: Int = 0
)
