package com.memowave.app.domain.usecase.settings

import com.memowave.app.domain.repository.SettingsRepository
import javax.inject.Inject

class ResetFsrsToDefaultsUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke() = repository.resetFsrsToDefaults()
}
