package com.memowave.app.domain.usecase.settings

import com.memowave.app.domain.model.settings.ThemeMode
import com.memowave.app.domain.repository.SettingsRepository
import javax.inject.Inject

class UpdateThemeUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(mode: ThemeMode) = repository.setThemeMode(mode)
}
