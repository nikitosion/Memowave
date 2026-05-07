package com.memowave.app.ui.screen.settings.appearance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memowave.app.domain.model.settings.ThemeMode
import com.memowave.app.domain.usecase.settings.GetSettingsUseCase
import com.memowave.app.domain.usecase.settings.UpdateAppLanguageUseCase
import com.memowave.app.domain.usecase.settings.UpdateReduceMotionUseCase
import com.memowave.app.domain.usecase.settings.UpdateThemeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppearanceViewModel @Inject constructor(
    getSettingsUseCase: GetSettingsUseCase,
    private val updateThemeUseCase: UpdateThemeUseCase,
    private val updateAppLanguageUseCase: UpdateAppLanguageUseCase,
    private val updateReduceMotionUseCase: UpdateReduceMotionUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppearanceUiState())
    val uiState: StateFlow<AppearanceUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getSettingsUseCase().collect { settings ->
                _uiState.update {
                    it.copy(
                        themeMode = settings.themeMode,
                        appLanguage = settings.appLanguage,
                        reduceMotion = settings.reduceMotion
                    )
                }
            }
        }
    }

    fun selectTheme(mode: ThemeMode) {
        viewModelScope.launch { updateThemeUseCase(mode) }
    }

    fun selectLanguage(language: String?) {
        _uiState.update { it.copy(languageSheetShown = false) }
        viewModelScope.launch { updateAppLanguageUseCase(language) }
    }

    fun toggleReduceMotion(enabled: Boolean) {
        viewModelScope.launch { updateReduceMotionUseCase(enabled) }
    }

    fun showLanguageSheet() {
        _uiState.update { it.copy(languageSheetShown = true) }
    }

    fun dismissLanguageSheet() {
        _uiState.update { it.copy(languageSheetShown = false) }
    }
}
