package com.memowave.app.ui.screen.main_page

import com.memowave.app.domain.model.streak.StreakState

data class MainPageUiState(
    val isLoading: Boolean = false,
    val newCount: Int = 0,
    val dueCount: Int = 0,
    val learnedCount: Int = 0,
    val lastLearningModeId: String = "Каротчки",
    val factIndex: Int = 0,
    val streak: StreakState = StreakState.EMPTY,
)
