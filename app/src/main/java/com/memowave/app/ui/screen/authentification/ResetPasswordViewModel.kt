package com.memowave.app.ui.screen.authentification

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memowave.app.R
import com.memowave.app.core.util.StringProvider
import com.memowave.app.domain.usecase.auth.ResetPasswordUseCase
import com.memowave.app.ui.common.notification.NotificationManager
import com.memowave.app.ui.screen.authentification.components.ui_state.ResetPasswordFormState
import com.memowave.app.ui.screen.authentification.components.ui_state.ResetPasswordUiState
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
 * ViewModel for [ResetPasswordScreen].
 *
 * Receives the target [userId] via the `userId` nav argument (read here through
 * [SavedStateHandle]). The route registration in
 * [com.memowave.app.ui.navigation.NavGraph] must declare this argument.
 */
@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val formValidator: AuthFormValidator,
    private val notificationManager: NotificationManager,
    private val strings: StringProvider,
    private val resetPasswordUseCase: ResetPasswordUseCase,
) : ViewModel() {

    private val userId: Long = checkNotNull(savedStateHandle.get<Long>(USER_ID_ARG)) {
        "$USER_ID_ARG argument is required for ResetPasswordViewModel"
    }

    private val _uiState = MutableStateFlow(ResetPasswordUiState())
    val uiState: StateFlow<ResetPasswordUiState> = _uiState.asStateFlow()

    private val _navEvents = Channel<AuthNavEvent>(Channel.BUFFERED)
    val navEvents: Flow<AuthNavEvent> = _navEvents.receiveAsFlow()

    val isButtonEnabled: StateFlow<Boolean> = uiState
        .map {
            with(it.form) { isNewPasswordValid && isRepeatedNewPasswordValid } && !it.isLoading
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    fun onNewPasswordChanged(newPassword: String) = updateForm {
        formValidator.validateResetNewPassword(it, newPassword)
    }

    fun onRepeatedNewPasswordChanged(newRepeatedPassword: String) = updateForm {
        formValidator.validateResetRepeatedPassword(it, newRepeatedPassword)
    }

    fun onResetClick() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            when (val result = performResetPassword(userId, _uiState.value.form.newPassword)) {
                is AuthResult.Success -> {
                    _uiState.update {
                        it.copy(form = ResetPasswordFormState(), isLoading = false)
                    }
                    notificationManager.showSuccess(strings.getString(R.string.auth_success_password_reset))
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

    private inline fun updateForm(
        transform: (ResetPasswordFormState) -> ResetPasswordFormState
    ) {
        _uiState.update { it.copy(form = transform(it.form)) }
    }

    private suspend fun performResetPassword(
        userId: Long,
        newPassword: String
    ): AuthResult<Unit> = try {
        val result = resetPasswordUseCase(userId, newPassword)
        if (result.isSuccess) AuthResult.Success(Unit)
        else AuthResult.Failure(strings.getString(R.string.auth_error_reset_password_generic))
    } catch (e: Exception) {
        AuthResult.Error(e.message ?: strings.getString(R.string.auth_error_reset_password_failed))
    }

    companion object {
        const val USER_ID_ARG = "userId"
    }
}
