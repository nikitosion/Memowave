package com.memowave.app.domain.usecase.settings

import com.memowave.app.domain.repository.SettingsRepository
import javax.inject.Inject

class UpdateReduceMotionUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(enabled: Boolean) =
        repository.setReduceMotion(enabled)
}
