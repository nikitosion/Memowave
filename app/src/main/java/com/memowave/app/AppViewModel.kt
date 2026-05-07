package com.memowave.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memowave.app.core.auth.AuthStateManager
import com.memowave.app.domain.model.settings.AppSettings
import com.memowave.app.domain.model.settings.ThemeMode
import com.memowave.app.domain.usecase.settings.GetSettingsUseCase
import com.memowave.app.ui.common.notification.NotificationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    val authStateManager: AuthStateManager,
    val notificationManager: NotificationManager,
    getSettingsUseCase: GetSettingsUseCase
) : ViewModel() {

    private val settings: StateFlow<AppSettings> = getSettingsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = AppSettings()
        )

    val themeMode: StateFlow<ThemeMode> = settings
        .map { it.themeMode }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = ThemeMode.SYSTEM
        )

    val reduceMotion: StateFlow<Boolean> = settings
        .map { it.reduceMotion }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = false
        )
}
