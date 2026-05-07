package com.memowave.app.domain.usecase.settings

import com.memowave.app.domain.repository.SettingsRepository
import javax.inject.Inject

class UpdateGoalDaysUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    /**
     * @param days set of ISO day-of-week values (1=Mon..7=Sun). Empty set is rejected;
     * call is silently ignored to avoid leaving user with no scheduled days.
     */
    suspend operator fun invoke(days: Set<Int>) {
        if (days.isEmpty()) return
        repository.setGoalDaysOfWeek(days)
    }
}
