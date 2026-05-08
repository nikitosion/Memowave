package com.memowave.app.domain.model.streak

data class StreakState(
    val currentStreak: Int,
    val longestStreak: Int,
    val dailyTarget: Int,
    val wordsCompletedToday: Int,
    val isAlive: Boolean,
) {
    val wordsRemainingToday: Int = (dailyTarget - wordsCompletedToday).coerceAtLeast(0)
    val isTodayCompleted: Boolean = wordsCompletedToday >= dailyTarget

    companion object {
        val EMPTY = StreakState(
            currentStreak = 0,
            longestStreak = 0,
            dailyTarget = 0,
            wordsCompletedToday = 0,
            isAlive = false,
        )
    }
}
