package com.memowave.app.ui.screen.authentification

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memowave.app.domain.usecase.auth.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    /*// TODO: Remove automatic login on init
    init {
        onLoginClick()
    }*/

    val isLoginButtonEnabled: StateFlow<Boolean> = uiState
        .map { it.isEmailValid && it.isPasswordValid && !it.isLoading }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    fun onEmailChanged(newEmail: String) {
        val isValid = validateEmail(newEmail)
        _uiState.update {
            it.copy(
                email = newEmail,
                isEmailValid = isValid,
                emailError = if (isValid || newEmail.isEmpty()) null else "Некорректный email"
            )
        }
    }

    fun onPasswordChanged(newPassword: String) {
        val isValid = validatePassword(newPassword)
        _uiState.update {
            it.copy(
                password = newPassword,
                isPasswordValid = isValid,
                passwordError = if (isValid || newPassword.isEmpty()) null else "Пароль должен быть минимум 6 символов"
            )
        }
    }

    fun onLoginClick() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, authError = null) }

            try {
                val result = loginUseCase(
                    email = _uiState.value.email,
                    password = _uiState.value.password
                )

                if (result.isSuccess) {
                    _uiState.update { it.copy(isLoginSuccess = true, isLoading = false) }
                } else {
                    _uiState.update {
                        it.copy(
                            authError = "Что-то пошло не так при входе",
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        authError = e.message ?: "Ошибка при входе",
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun validateEmail(email: String): Boolean {
        return email.isNotEmpty() &&
                Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun validatePassword(password: String): Boolean {
        return password.length >= 6
    }
}
