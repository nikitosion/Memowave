package com.memowave.app.ui.screen.authentification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memowave.app.R
import com.memowave.app.core.util.StringProvider
import com.memowave.app.domain.usecase.auth.LoginUseCase
import com.memowave.app.ui.common.notification.NotificationManager
import com.memowave.app.ui.screen.authentification.components.ui_state.LoginFormState
import com.memowave.app.ui.screen.authentification.components.ui_state.LoginUiState
import com.memowave.app.ui.screen.authentification.helper.AuthFormValidator
import com.memowave.app.ui.screen.authentification.helper.AuthNavEvent
import com.memowave.app.ui.screen.authentification.helper.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for [LoginScreen].
 *
 * Owns the active login flow only — input validation and the sign-in request.
 * The auto-login (session bootstrap) probe lives in
 * [com.memowave.app.ui.screen.splash.SplashViewModel].
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val formValidator: AuthFormValidator,
    private val notificationManager: NotificationManager,
    private val strings: StringProvider,
    private val loginUseCase: LoginUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _navEvents = Channel<AuthNavEvent>(Channel.BUFFERED)
    val navEvents: Flow<AuthNavEvent> = _navEvents.receiveAsFlow()

    val isButtonEnabled: StateFlow<Boolean> = uiState
        .map {
            with(it.form) { isEmailValid && isPasswordValid } && !it.isLoading
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    fun onEmailChanged(newEmail: String) = updateForm {
        formValidator.validateLoginEmail(it, newEmail)
    }

    fun onPasswordChanged(newPassword: String) = updateForm {
        formValidator.validateLoginPassword(it, newPassword)
    }

    fun onLoginClick() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val email = _uiState.value.form.email
            val password = _uiState.value.form.password

            when (val result = performLogin(email, password)) {
                is AuthResult.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _navEvents.send(AuthNavEvent.ToMain)
                }

                is AuthResult.Failure -> {
                    _uiState.update {
                        it.copy(
                            form = it.form.copy(
                                emailError = result.message,
                                passwordError = result.message
                            ),
                            isLoading = false
                        )
                    }
                    notificationManager.showError(result.message)
                }

                is AuthResult.Error -> {
                    _uiState.update { it.copy(isLoading = false) }
                    notificationManager.showError(result.message)
                }
            }
        }
    }

    private inline fun updateForm(transform: (LoginFormState) -> LoginFormState) {
        _uiState.update { it.copy(form = transform(it.form)) }
    }

    private suspend fun performLogin(email: String, password: String): AuthResult<Unit> = try {
        val result = loginUseCase(email = email, password = password)
        if (result.isSuccess) AuthResult.Success(Unit)
        else AuthResult.Failure(strings.getString(R.string.auth_error_invalid_credentials))
    } catch (e: Exception) {
        AuthResult.Error(e.message ?: strings.getString(R.string.auth_error_login_failed))
    }
}
