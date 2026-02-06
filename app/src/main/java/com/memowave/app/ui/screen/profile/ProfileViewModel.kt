package com.memowave.app.ui.screen.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memowave.app.domain.usecase.auth.LogoutUseCase
import com.memowave.app.domain.usecase.profile.GetUserInfoUseCase
import com.memowave.app.ui.screen.profile.ui_state.ProfileUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: MutableStateFlow<ProfileUiState> = _uiState

    fun loadUserProfile(userId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = "") }

            val userProfileInfoResult = getUserInfoUseCase()
            when (userProfileInfoResult.isSuccess) {
                true -> {
                    val userProfile = userProfileInfoResult.getOrNull()
                    if (userProfile != null) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                user = userProfile,
                                errorMessage = ""
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "User profile not found"
                            )
                        }
                    }
                }

                false -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = userProfileInfoResult.exceptionOrNull()?.message
                                ?: "Unknown error occurred"
                        )
                    }
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
        }
    }
}