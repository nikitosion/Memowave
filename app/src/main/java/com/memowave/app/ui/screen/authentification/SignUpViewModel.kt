package com.memowave.app.ui.screen.authentification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memowave.app.R
import com.memowave.app.core.util.StringProvider
import com.memowave.app.domain.usecase.auth.SignUpUseCase
import com.memowave.app.ui.common.notification.NotificationManager
import com.memowave.app.ui.screen.authentification.components.ui_state.SignUpFormState
import com.memowave.app.ui.screen.authentification.components.ui_state.SignUpUiState
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

/** ViewModel for [SignUpScreen]. */
@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val formValidator: AuthFormValidator,
    private val notificationManager: NotificationManager,
    private val strings: StringProvider,
    private val signUpUseCase: SignUpUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    private val _navEvents = Channel<AuthNavEvent>(Channel.BUFFERED)
    val navEvents: Flow<AuthNavEvent> = _navEvents.receiveAsFlow()

    val isButtonEnabled: StateFlow<Boolean> = uiState
        .map {
            with(it.form) {
                isNameValid && isEmailValid && isPasswordValid && isRepeatedPasswordValid
            } && !it.isLoading
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    fun onNameChanged(newName: String) = updateForm {
        formValidator.validateSignUpName(it, newName)
    }

    fun onEmailChanged(newEmail: String) = updateForm {
        formValidator.validateSignUpEmail(it, newEmail)
    }

    fun onPasswordChanged(newPassword: String) = updateForm {
        formValidator.validateSignUpPassword(it, newPassword)
    }

    fun onRepeatedPasswordChanged(newPassword: String) = updateForm {
        formValidator.validateSignUpRepeatedPassword(it, newPassword)
    }

    fun onSignUpClick() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val form = _uiState.value.form
            when (val result = performSignUp(form.username, form.email, form.password)) {
                is AuthResult.Success -> {
                    _uiState.update { it.copy(form = SignUpFormState(), isLoading = false) }
                    notificationManager.showSuccess(strings.getString(R.string.notification_registration_success))
                    _navEvents.send(AuthNavEvent.BackToLogin)
                }

                is AuthResult.Failure -> {
                    _uiState.update { it.copy(isLoading = false) }
                    notificationManager.showError(result.message)
                }

                is AuthResult.Error -> {
                    _uiState.update { it.copy(isLoading = false) }
                    notificationManager.showError(result.message)
                }
            }
        }
    }

    private inline fun updateForm(transform: (SignUpFormState) -> SignUpFormState) {
        _uiState.update { it.copy(form = transform(it.form)) }
    }

    private suspend fun performSignUp(
        username: String,
        email: String,
        password: String
    ): AuthResult<Unit> = try {
        val result = signUpUseCase(username = username, email = email, password = password)
        if (result.isSuccess) AuthResult.Success(Unit)
        else AuthResult.Failure(strings.getString(R.string.auth_error_signup_failed_generic))
    } catch (e: Exception) {
        AuthResult.Error(e.message ?: strings.getString(R.string.auth_error_signup_failed))
    }
}
