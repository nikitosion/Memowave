package com.memowave.app.domain.usecase.settings

import com.memowave.app.domain.repository.SettingsRepository
import javax.inject.Inject

class UpdateAppLanguageUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    /**
     * @param language null → system; "ru", "en", etc. → explicit language tag.
     */
    suspend operator fun invoke(language: String?) =
        repository.setAppLanguage(language)
}
