package com.memowave.app.ui.screen.settings.algorithm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memowave.app.domain.usecase.settings.GetSettingsUseCase
import com.memowave.app.domain.usecase.settings.ResetFsrsToDefaultsUseCase
import com.memowave.app.domain.usecase.settings.UpdateFsrsConfigUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlgorithmViewModel @Inject constructor(
    getSettingsUseCase: GetSettingsUseCase,
    private val updateFsrsConfigUseCase: UpdateFsrsConfigUseCase,
    private val resetFsrsToDefaultsUseCase: ResetFsrsToDefaultsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AlgorithmUiState())
    val uiState: StateFlow<AlgorithmUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getSettingsUseCase().collect { settings ->
                _uiState.update {
                    it.copy(
                        requestRetention = settings.fsrsRequestRetention.toFloat(),
                        maximumInterval = settings.fsrsMaximumInterval,
                        easyBonus = settings.fsrsEasyBonus.toFloat(),
                        hardPenalty = settings.fsrsHardPenalty.toFloat()
                    )
                }
            }
        }
    }

    fun onRetentionChange(value: Float) {
        _uiState.update { it.copy(requestRetention = value) }
    }

    fun onRetentionCommit() {
        viewModelScope.launch {
            updateFsrsConfigUseCase(requestRetention = _uiState.value.requestRetention.toDouble())
        }
    }

    fun onMaxIntervalChange(value: Float) {
        _uiState.update { it.copy(maximumInterval = value.toInt()) }
    }

    fun onMaxIntervalCommit() {
        viewModelScope.launch {
            updateFsrsConfigUseCase(maximumInterval = _uiState.value.maximumInterval)
        }
    }

    fun onEasyBonusChange(value: Float) {
        _uiState.update { it.copy(easyBonus = value) }
    }

    fun onEasyBonusCommit() {
        viewModelScope.launch {
            updateFsrsConfigUseCase(easyBonus = _uiState.value.easyBonus.toDouble())
        }
    }

    fun onHardPenaltyChange(value: Float) {
        _uiState.update { it.copy(hardPenalty = value) }
    }

    fun onHardPenaltyCommit() {
        viewModelScope.launch {
            updateFsrsConfigUseCase(hardPenalty = _uiState.value.hardPenalty.toDouble())
        }
    }

    fun showResetDialog() {
        _uiState.update { it.copy(resetDialogShown = true) }
    }

    fun dismissResetDialog() {
        _uiState.update { it.copy(resetDialogShown = false) }
    }

    fun confirmReset() {
        _uiState.update { it.copy(resetDialogShown = false) }
        viewModelScope.launch { resetFsrsToDefaultsUseCase() }
    }
}
