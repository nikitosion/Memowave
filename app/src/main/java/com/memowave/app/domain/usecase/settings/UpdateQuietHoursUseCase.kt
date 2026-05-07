package com.memowave.app.domain.usecase.settings

import com.memowave.app.domain.repository.SettingsRepository
import javax.inject.Inject

class UpdateQuietHoursUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend fun setEnabled(enabled: Boolean) =
        repository.setQuietHoursEnabled(enabled)

    suspend fun setRange(
        startHour: Int,
        startMinute: Int,
        endHour: Int,
        endMinute: Int
    ) = repository.setQuietHours(startHour, startMinute, endHour, endMinute)
}
