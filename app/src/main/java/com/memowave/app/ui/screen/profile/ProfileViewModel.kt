package com.memowave.app.ui.screen.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memowave.app.domain.usecase.profile.GetUserInfoUseCase
import com.memowave.app.domain.usecase.streak.GetStreakStateUseCase
import com.memowave.app.ui.screen.profile.ui_state.ProfileUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val getStreakStateUseCase: GetStreakStateUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        observeStreak()
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = getUserInfoUseCase()
            _uiState.update {
                it.copy(
                    isLoading = false,
                    user = result.getOrNull() ?: it.user,
                )
            }
        }
    }

    private fun observeStreak() {
        viewModelScope.launch {
            getStreakStateUseCase().collect { streak ->
                _uiState.update { it.copy(streak = streak) }
            }
        }
    }
}
