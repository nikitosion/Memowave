package com.memowave.app.ui.screen.main_page

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memowave.app.domain.algorithm.CardPhase
import com.memowave.app.domain.repository.SettingsRepository
import com.memowave.app.domain.usecase.word.GetWordsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDateTime
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private const val LEARNED_INTERVAL_DAYS = 21

@HiltViewModel
class MainPageViewModel @Inject constructor(
    private val getWordsUseCase: GetWordsUseCase,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainPageUiState())
    val uiState: StateFlow<MainPageUiState> = _uiState.asStateFlow()

    init {
        loadLastMode()
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val words = getWordsUseCase().getOrNull().orEmpty()
            val now = LocalDateTime.now()
            val newCount = words.count { it.phase == CardPhase.Added.value }
            val dueCount = words.count {
                it.phase != CardPhase.Added.value && !it.dueDate.isAfter(now)
            }
            val learnedCount = words.count {
                it.phase == CardPhase.Review.value && it.interval >= LEARNED_INTERVAL_DAYS
            }
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                newCount = newCount,
                dueCount = dueCount,
                learnedCount = learnedCount,
            )
        }
    }

    fun cycleFact(delta: Int, totalFacts: Int) {
        if (totalFacts <= 0) return
        val current = _uiState.value.factIndex
        val next = ((current + delta) % totalFacts + totalFacts) % totalFacts
        _uiState.value = _uiState.value.copy(factIndex = next)
    }

    private fun loadLastMode() {
        viewModelScope.launch {
            val settings = settingsRepository.getSettings().first()
            _uiState.value = _uiState.value.copy(
                lastLearningModeId = settings.lastLearningModeId
            )
        }
    }

    fun onLearningModeLaunched(modeId: String) {
        viewModelScope.launch { settingsRepository.setLastLearningMode(modeId) }
        _uiState.value = _uiState.value.copy(lastLearningModeId = modeId)
    }
}
