package com.memowave.app.ui.screen.settings.appearance

import com.memowave.app.domain.model.settings.ThemeMode

data class AppearanceUiState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val appLanguage: String? = null,
    val reduceMotion: Boolean = false,
    val languageSheetShown: Boolean = false
)
