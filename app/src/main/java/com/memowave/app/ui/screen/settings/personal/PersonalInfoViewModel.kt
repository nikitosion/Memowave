package com.memowave.app.ui.screen.settings.personal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memowave.app.domain.usecase.auth.ChangePasswordError
import com.memowave.app.domain.usecase.auth.ChangePasswordUseCase
import com.memowave.app.domain.usecase.auth.DeleteAccountUseCase
import com.memowave.app.domain.usecase.profile.GetUserInfoUseCase
import com.memowave.app.domain.usecase.profile.UpdateUsernameUseCase
import com.memowave.app.domain.validator.PasswordValidator
import com.memowave.app.ui.common.notification.NotificationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PersonalInfoViewModel @Inject constructor(
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val updateUsernameUseCase: UpdateUsernameUseCase,
    private val changePasswordUseCase: ChangePasswordUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val passwordValidator: PasswordValidator,
    private val notificationManager: NotificationManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(PersonalInfoUiState())
    val uiState: StateFlow<PersonalInfoUiState> = _uiState.asStateFlow()

    fun loadUser() {
        viewModelScope.launch {
            _uiState.update { it.copy(isUserLoading = true, userError = null) }
            getUserInfoUseCase()
                .onSuccess { user ->
                    _uiState.update {
                        it.copy(
                            isUserLoading = false,
                            user = user,
                            userError = null,
                            editingUsername = user.username.orEmpty(),
                            usernameError = null
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(isUserLoading = false, userError = error.message)
                    }
                }
        }
    }

    fun onUsernameChange(value: String) {
        _uiState.update { it.copy(editingUsername = value, usernameError = null) }
    }

    fun cancelUsernameEdit() {
        _uiState.update {
            it.copy(
                editingUsername = it.user?.username.orEmpty(),
                usernameError = null
            )
        }
    }

    fun submitUsername(
        successMessage: String,
        errorTooShort: String,
        errorTooLong: String,
        errorGeneric: String
    ) {
        val newUsername = _uiState.value.editingUsername.trim()
        if (newUsername == _uiState.value.originalUsername) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmittingUsername = true, usernameError = null) }
            val result = updateUsernameUseCase(newUsername)
            result
                .onSuccess { user ->
                    _uiState.update {
                        it.copy(
                            isSubmittingUsername = false,
                            user = user,
                            editingUsername = user.username.orEmpty(),
                            usernameError = null
                        )
                    }
                    notificationManager.showSuccess(successMessage)
                }
                .onFailure { error ->
                    val message = when (error.message) {
                        UpdateUsernameUseCase.ERR_TOO_SHORT -> errorTooShort
                        UpdateUsernameUseCase.ERR_TOO_LONG -> errorTooLong
                        else -> errorGeneric
                    }
                    _uiState.update {
                        it.copy(isSubmittingUsername = false, usernameError = message)
                    }
                    notificationManager.showError(message)
                }
        }
    }

    fun togglePasswordExpanded() {
        _uiState.update {
            if (it.passwordExpanded) {
                it.copy(
                    passwordExpanded = false,
                    currentPassword = "",
                    newPassword = "",
                    repeatPassword = "",
                    passwordValidation = passwordValidator.validate("").validationState,
                    passwordsMatch = false
                )
            } else {
                it.copy(passwordExpanded = true)
            }
        }
    }

    fun onCurrentPasswordChange(value: String) {
        _uiState.update { it.copy(currentPassword = value) }
    }

    fun onNewPasswordChange(value: String) {
        val validation = passwordValidator.validate(value).validationState
        _uiState.update {
            it.copy(
                newPassword = value,
                passwordValidation = validation,
                passwordsMatch = value.isNotEmpty() && value == it.repeatPassword
            )
        }
    }

    fun onRepeatPasswordChange(value: String) {
        _uiState.update {
            it.copy(
                repeatPassword = value,
                passwordsMatch = value.isNotEmpty() && value == it.newPassword
            )
        }
    }

    fun submitPassword(
        successMessage: String,
        errorWrong: String,
        errorForbidden: String,
        errorServer: String,
        errorSame: String,
        errorInvalid: String,
        errorUnknown: String
    ) {
        val state = _uiState.value
        if (!state.canSubmitPassword) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }
            val result = changePasswordUseCase(
                currentPassword = state.currentPassword,
                newPassword = state.newPassword
            )
            _uiState.update { it.copy(isSubmitting = false) }

            result
                .onSuccess {
                    notificationManager.showSuccess(successMessage)
                    _uiState.update {
                        it.copy(
                            passwordExpanded = false,
                            currentPassword = "",
                            newPassword = "",
                            repeatPassword = "",
                            passwordValidation = passwordValidator.validate("").validationState,
                            passwordsMatch = false
                        )
                    }
                }
                .onFailure { error ->
                    val message = when (error) {
                        is ChangePasswordError.WrongCurrentPassword -> errorWrong
                        is ChangePasswordError.Forbidden -> errorForbidden
                        is ChangePasswordError.ServerError -> errorServer
                        is ChangePasswordError.SamePassword -> errorSame
                        is ChangePasswordError.InvalidPassword -> errorInvalid
                        else -> errorUnknown
                    }
                    notificationManager.showError(message)
                }
        }
    }

    fun showDeleteDialog() {
        _uiState.update { it.copy(deleteDialogShown = true) }
    }

    fun dismissDeleteDialog() {
        _uiState.update { it.copy(deleteDialogShown = false) }
    }

    fun confirmDelete(comingSoonMessage: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(deleteDialogShown = false) }
            val result = deleteAccountUseCase()
            result.onFailure {
                notificationManager.showInfo(comingSoonMessage)
            }
        }
    }
}
