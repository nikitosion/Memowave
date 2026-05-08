package com.memowave.app.ui.screen.authentification

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memowave.app.R
import com.memowave.app.core.util.StringProvider
import com.memowave.app.domain.usecase.auth.SendCodeUseCase
import com.memowave.app.domain.usecase.auth.VerifyEmailError
import com.memowave.app.domain.usecase.auth.VerifyEmailUseCase
import com.memowave.app.ui.common.notification.NotificationManager
import com.memowave.app.ui.screen.authentification.components.ui_state.VerifyEmailFormState
import com.memowave.app.ui.screen.authentification.components.ui_state.VerifyEmailUiState
import com.memowave.app.ui.screen.authentification.helper.AuthNavEvent
import com.memowave.app.ui.screen.authentification.helper.AuthResult
import com.memowave.app.ui.screen.authentification.helper.VerifyEmailFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
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
 * ViewModel for [VerifyEmailScreen].
 *
 * Receives [USER_ID_ARG] and [FLOW_ARG] via [SavedStateHandle]. The route registration
 * in [com.memowave.app.ui.navigation.NavGraph] must declare both arguments.
 */
@HiltViewModel
class VerifyEmailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val notificationManager: NotificationManager,
    private val strings: StringProvider,
    private val verifyEmailUseCase: VerifyEmailUseCase,
    private val sendCodeUseCase: SendCodeUseCase,
) : ViewModel() {

    private val userId: Long = checkNotNull(savedStateHandle.get<Long>(USER_ID_ARG)) {
        "$USER_ID_ARG argument is required for VerifyEmailViewModel"
    }

    val flow: VerifyEmailFlow = run {
        val raw = checkNotNull(savedStateHandle.get<String>(FLOW_ARG)) {
            "$FLOW_ARG argument is required for VerifyEmailViewModel"
        }
        VerifyEmailFlow.valueOf(raw)
    }

    private val _uiState = MutableStateFlow(VerifyEmailUiState())
    val uiState: StateFlow<VerifyEmailUiState> = _uiState.asStateFlow()

    private val _navEvents = Channel<AuthNavEvent>(Channel.BUFFERED)
    val navEvents: Flow<AuthNavEvent> = _navEvents.receiveAsFlow()

    val isButtonEnabled: StateFlow<Boolean> = uiState
        .map { it.form.code.length == CODE_LENGTH && !it.isLoading }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    private var cooldownJob: Job? = null

    fun onCodeChanged(newCode: String) {
        val sanitized = newCode.filter(Char::isDigit).take(CODE_LENGTH)
        _uiState.update {
            it.copy(form = it.form.copy(code = sanitized, codeError = null))
        }
    }

    fun onSubmit() {
        viewModelScope.launch {
            val code = _uiState.value.form.code
            if (code.length != CODE_LENGTH) return@launch

            _uiState.update { it.copy(isLoading = true) }

            when (val result = performVerify(code)) {
                is AuthResult.Success -> {
                    _uiState.update { it.copy(form = VerifyEmailFormState(), isLoading = false) }
                    val event = when (flow) {
                        VerifyEmailFlow.SignUp -> AuthNavEvent.ToMain
                        VerifyEmailFlow.ForgotPassword -> AuthNavEvent.ToResetPassword(userId)
                    }
                    _navEvents.send(event)
                }

                is AuthResult.Failure -> {
                    _uiState.update {
                        it.copy(
                            form = it.form.copy(codeError = result.message),
                            isLoading = false,
                        )
                    }
                }

                is AuthResult.Error -> {
                    _uiState.update { it.copy(isLoading = false) }
                    notificationManager.showError(result.message)
                }
            }
        }
    }

    fun onResendCode() {
        if (_uiState.value.resendCooldownSec > 0) return
        viewModelScope.launch {
            val result = sendCodeUseCase(userId)
            if (result.isSuccess) {
                notificationManager.showSuccess(
                    strings.getString(R.string.auth_verify_email_resend_success)
                )
                startResendCooldown()
            } else {
                notificationManager.showError(
                    strings.getString(R.string.auth_verify_email_send_failed)
                )
            }
        }
    }

    private fun startResendCooldown() {
        cooldownJob?.cancel()
        cooldownJob = viewModelScope.launch {
            for (remaining in RESEND_COOLDOWN_SEC downTo 1) {
                _uiState.update { it.copy(resendCooldownSec = remaining) }
                delay(1_000)
            }
            _uiState.update { it.copy(resendCooldownSec = 0) }
        }
    }

    private suspend fun performVerify(code: String): AuthResult<Unit> = try {
        val result = verifyEmailUseCase(userId, code)
        when {
            result.isSuccess -> AuthResult.Success(Unit)
            else -> when (val cause = result.exceptionOrNull()) {
                is VerifyEmailError.InvalidCode ->
                    AuthResult.Failure(strings.getString(R.string.auth_verify_email_invalid_code))
                is VerifyEmailError -> // Forbidden / Unknown / SendFailed
                    AuthResult.Failure(strings.getString(R.string.auth_verify_email_failed_generic))
                else -> AuthResult.Error(
                    cause?.message ?: strings.getString(R.string.auth_verify_email_failed_generic)
                )
            }
        }
    } catch (e: Exception) {
        AuthResult.Error(
            e.message ?: strings.getString(R.string.auth_verify_email_failed_generic)
        )
    }

    companion object {
        const val USER_ID_ARG = "userId"
        const val FLOW_ARG = "flow"
        const val CODE_LENGTH = 5
        const val RESEND_COOLDOWN_SEC = 60
    }
}
