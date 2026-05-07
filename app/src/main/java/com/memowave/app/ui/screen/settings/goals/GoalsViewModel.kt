package com.memowave.app.ui.screen.settings.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memowave.app.domain.usecase.settings.GetSettingsUseCase
import com.memowave.app.domain.usecase.settings.UpdateGoalDaysUseCase
import com.memowave.app.domain.usecase.settings.UpdateReminderTimeUseCase
import com.memowave.app.domain.usecase.settings.UpdateWeeklyGoalUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GoalsViewModel @Inject constructor(
    getSettingsUseCase: GetSettingsUseCase,
    private val updateWeeklyGoalUseCase: UpdateWeeklyGoalUseCase,
    private val updateGoalDaysUseCase: UpdateGoalDaysUseCase,
    private val updateReminderTimeUseCase: UpdateReminderTimeUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(GoalsUiState())
    val uiState: StateFlow<GoalsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getSettingsUseCase().collect { settings ->
                _uiState.update {
                    it.copy(
                        weeklyWordGoal = settings.weeklyWordGoal,
                        goalDays = settings.goalDaysOfWeek,
                        reminderHour = settings.reminderHour,
                        reminderMinute = settings.reminderMinute
                    )
                }
            }
        }
    }

    fun onWeeklyGoalChange(words: Int) {
        viewModelScope.launch { updateWeeklyGoalUseCase(words) }
    }

    fun onToggleDay(iso: Int) {
        val current = _uiState.value.goalDays
        val next = if (iso in current) current - iso else current + iso
        if (next.isEmpty()) return
        viewModelScope.launch { updateGoalDaysUseCase(next) }
    }

    fun onReminderTimeChange(hour: Int, minute: Int) {
        viewModelScope.launch { updateReminderTimeUseCase(hour, minute) }
    }
}
