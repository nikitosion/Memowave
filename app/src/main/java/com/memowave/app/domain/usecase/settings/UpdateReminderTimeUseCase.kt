package com.memowave.app.domain.usecase.settings

import com.memowave.app.domain.repository.SettingsRepository
import javax.inject.Inject

class UpdateReminderTimeUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(hour: Int, minute: Int) =
        repository.setReminderTime(hour, minute)
}
