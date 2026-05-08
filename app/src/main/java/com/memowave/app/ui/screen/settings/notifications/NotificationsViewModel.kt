package com.memowave.app.ui.screen.settings.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memowave.app.domain.usecase.settings.GetSettingsUseCase
import com.memowave.app.domain.usecase.settings.UpdateMilestonesUseCase
import com.memowave.app.domain.usecase.settings.UpdateNotificationsUseCase
import com.memowave.app.domain.usecase.settings.UpdateQuietHoursUseCase
import com.memowave.app.domain.usecase.settings.UpdateStreakRiskUseCase
import com.memowave.app.domain.usecase.settings.UpdateStudyRemindersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    getSettingsUseCase: GetSettingsUseCase,
    private val updateNotificationsUseCase: UpdateNotificationsUseCase,
    private val updateStudyRemindersUseCase: UpdateStudyRemindersUseCase,
    private val updateStreakRiskUseCase: UpdateStreakRiskUseCase,
    private val updateMilestonesUseCase: UpdateMilestonesUseCase,
    private val updateQuietHoursUseCase: UpdateQuietHoursUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getSettingsUseCase().collect { s ->
                _uiState.update {
                    it.copy(
                        masterEnabled = s.notificationsEnabled,
                        studyRemindersEnabled = s.studyRemindersEnabled,
                        streakRiskEnabled = s.streakRiskEnabled,
                        milestonesEnabled = s.milestonesEnabled,
                        quietHoursEnabled = s.quietHoursEnabled,
                        quietStartHour = s.quietStartHour,
                        quietStartMinute = s.quietStartMinute,
                        quietEndHour = s.quietEndHour,
                        quietEndMinute = s.quietEndMinute,
                        reminderHour = s.reminderHour,
                        reminderMinute = s.reminderMinute,
                    )
                }
            }
        }
    }

    fun setMasterEnabled(enabled: Boolean) {
        _uiState.update { it.copy(permissionDenied = false) }
        viewModelScope.launch { updateNotificationsUseCase(enabled) }
    }

    fun onPermissionDenied() {
        _uiState.update { it.copy(masterEnabled = false, permissionDenied = true) }
        viewModelScope.launch { updateNotificationsUseCase(false) }
    }

    fun dismissPermissionRationale() {
        _uiState.update { it.copy(permissionDenied = false) }
    }

    fun setStudyReminders(enabled: Boolean) {
        viewModelScope.launch { updateStudyRemindersUseCase(enabled) }
    }

    fun setStreakRisk(enabled: Boolean) {
        viewModelScope.launch { updateStreakRiskUseCase(enabled) }
    }

    fun setMilestones(enabled: Boolean) {
        viewModelScope.launch { updateMilestonesUseCase(enabled) }
    }

    fun setQuietHoursEnabled(enabled: Boolean) {
        viewModelScope.launch { updateQuietHoursUseCase.setEnabled(enabled) }
    }

    fun setQuietStart(hour: Int, minute: Int) {
        val s = _uiState.value
        viewModelScope.launch {
            updateQuietHoursUseCase.setRange(hour, minute, s.quietEndHour, s.quietEndMinute)
        }
    }

    fun setQuietEnd(hour: Int, minute: Int) {
        val s = _uiState.value
        viewModelScope.launch {
            updateQuietHoursUseCase.setRange(s.quietStartHour, s.quietStartMinute, hour, minute)
        }
    }
}
