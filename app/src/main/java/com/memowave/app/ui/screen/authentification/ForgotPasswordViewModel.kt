package com.memowave.app.ui.screen.authentification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memowave.app.R
import com.memowave.app.core.util.StringProvider
import com.memowave.app.domain.usecase.auth.GetUserByEmailUseCase
import com.memowave.app.domain.usecase.auth.SendCodeUseCase
import com.memowave.app.ui.common.notification.NotificationManager
import com.memowave.app.ui.screen.authentification.components.ui_state.ForgotPasswordFormState
import com.memowave.app.ui.screen.authentification.components.ui_state.ForgotPasswordUiState
import com.memowave.app.ui.screen.authentification.helper.AuthFormValidator
import com.memowave.app.ui.screen.authentification.helper.AuthNavEvent
import com.memowave.app.ui.screen.authentification.helper.AuthResult
import com.memowave.app.ui.screen.authentification.helper.VerifyEmailFlow
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

/** ViewModel for [ForgotPasswordScreen]. */
@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val formValidator: AuthFormValidator,
    private val notificationManager: NotificationManager,
    private val strings: StringProvider,
    private val getUserByEmailUseCase: GetUserByEmailUseCase,
    private val sendCodeUseCase: SendCodeUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    private val _navEvents = Channel<AuthNavEvent>(Channel.BUFFERED)
    val navEvents: Flow<AuthNavEvent> = _navEvents.receiveAsFlow()

    val isButtonEnabled: StateFlow<Boolean> = uiState
        .map { it.form.isEmailValid && !it.isLoading }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    fun onEmailChanged(newEmail: String) = updateForm {
        formValidator.validateForgotPasswordEmail(it, newEmail)
    }

    fun onContinueClick() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            when (val result = performGetUserByEmail(_uiState.value.form.email)) {
                is AuthResult.Success -> {
                    val sendResult = sendCodeUseCase(result.data)
                    _uiState.update { it.copy(isLoading = false) }
                    if (sendResult.isSuccess) {
                        _navEvents.send(
                            AuthNavEvent.ToVerifyEmail(
                                userId = result.data,
                                flow = VerifyEmailFlow.ForgotPassword,
                            )
                        )
                    } else {
                        notificationManager.showError(
                            strings.getString(R.string.auth_verify_email_send_failed)
                        )
                    }
                }

                is AuthResult.Failure -> {
                    _uiState.update {
                        it.copy(
                            form = it.form.copy(emailError = result.message),
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

    private inline fun updateForm(
        transform: (ForgotPasswordFormState) -> ForgotPasswordFormState
    ) {
        _uiState.update { it.copy(form = transform(it.form)) }
    }

    private suspend fun performGetUserByEmail(email: String): AuthResult<Long> = try {
        val result = getUserByEmailUseCase(email)
        if (result.isSuccess) {
            val userId = result.getOrNull()?.id
            if (userId != null) AuthResult.Success(userId)
            else AuthResult.Failure(strings.getString(R.string.auth_error_user_not_found))
        } else {
            AuthResult.Failure(strings.getString(R.string.auth_error_invalid_email))
        }
    } catch (e: Exception) {
        AuthResult.Error(e.message ?: strings.getString(R.string.auth_error_get_user_failed))
    }
}
