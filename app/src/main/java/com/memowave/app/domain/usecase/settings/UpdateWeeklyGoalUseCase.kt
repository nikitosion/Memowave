package com.memowave.app.domain.usecase.settings

import com.memowave.app.domain.repository.SettingsRepository
import javax.inject.Inject

class UpdateWeeklyGoalUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(words: Int) = repository.setWeeklyGoal(words)
}
