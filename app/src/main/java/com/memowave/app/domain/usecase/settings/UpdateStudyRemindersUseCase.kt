package com.memowave.app.domain.usecase.settings

import com.memowave.app.domain.repository.SettingsRepository
import javax.inject.Inject

class UpdateStudyRemindersUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(enabled: Boolean) =
        repository.setStudyRemindersEnabled(enabled)
}
