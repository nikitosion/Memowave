package com.memowave.app.ui.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memowave.app.domain.usecase.auth.LogoutUseCase
import com.memowave.app.domain.usecase.profile.GetUserInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun loadUser() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = getUserInfoUseCase()
            result
                .onSuccess { user ->
                    _uiState.update { it.copy(isLoading = false, user = user, errorMessage = null) }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Unknown error"
                        )
                    }
                }
        }
    }

    fun showLogoutDialog() {
        _uiState.update { it.copy(logoutDialogShown = true) }
    }

    fun dismissLogoutDialog() {
        _uiState.update { it.copy(logoutDialogShown = false) }
    }

    fun confirmLogout() {
        _uiState.update { it.copy(logoutDialogShown = false) }
        viewModelScope.launch {
            logoutUseCase()
        }
    }
}
