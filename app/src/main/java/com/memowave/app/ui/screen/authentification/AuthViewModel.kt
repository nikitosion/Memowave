package com.memowave.app.ui.screen.authentification

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memowave.app.domain.usecase.auth.LoginUseCase
import com.memowave.app.ui.screen.authentification.components.ui_state.AuthUiState
import com.memowave.app.ui.screen.authentification.components.ui_state.ForgotPasswordFormState
import com.memowave.app.ui.screen.authentification.components.ui_state.PasswordValidationState
import com.memowave.app.ui.screen.authentification.components.ui_state.SignUpFormState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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
        .map {
            with(it.loginFormState) {
                isEmailValid && isPasswordValid
            } && !it.isLoading
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    val isForgotPasswordButtonEnabled: StateFlow<Boolean> = uiState
        .map {
            it.forgotPasswordForm.isEmailValid && !it.isLoading
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), true)

    val isResetPasswordButtonEnabled: StateFlow<Boolean> = uiState
        .map {
            with(it.forgotPasswordForm) {
                isNewPasswordValid && isRepeatedNewPasswordValid
            } && !it.isLoading
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), true)

    val isSignUpButtonEnabled: StateFlow<Boolean> = uiState
        .map {
            with(it.signUpFormState) {
                isNameValid && isEmailValid && isPasswordValid && isRepeatedPasswordValid
            } && !it.isLoading
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    fun onNameChanged(newName: String) {
        val isValid = validateName(newName)
        _uiState.update {
            it.copy(
                signUpFormState = it.signUpFormState.copy(
                    name = newName,
                    isNameValid = isValid,
                    nameError =
                        if (isValid || newName.isEmpty()) null else "Имя должно содержать минимум 2 символа"
                )
            )
        }
    }

    fun onLoginEmailChanged(newEmail: String) {
        val isValid = validateEmail(newEmail)
        _uiState.update {
            it.copy(
                loginFormState = it.loginFormState.copy(
                    email = newEmail,
                    isEmailValid = isValid,
                    emailError = null
                )
            )
        }
    }

    fun onForgotPasswordEmailChanged(newEmail: String) {
        val isValid = validateEmail(newEmail)
        _uiState.update {
            it.copy(
                forgotPasswordForm = it.forgotPasswordForm.copy(
                    email = newEmail,
                    isEmailValid = isValid,
                    emailError = null
                )
            )
        }
    }

    fun onSignUpEmailChanged(newEmail: String) {
        val isValid = validateEmail(newEmail)
        _uiState.update {
            it.copy(
                signUpFormState = it.signUpFormState.copy(
                    email = newEmail,
                    isEmailValid = isValid,
                    emailError = if (isValid || newEmail.isEmpty()) null else "Некорректный email"
                )
            )
        }
    }

    fun onLoginPasswordChanged(newPassword: String) {
        val isValid = validatePasswordLength(newPassword)
        _uiState.update {
            it.copy(
                loginFormState = it.loginFormState.copy(
                    password = newPassword,
                    isPasswordValid = isValid,
                    passwordError = null
                )
            )
        }
    }

    fun onSignUpPasswordChanged(newPassword: String) {
        val passwordValidation = validatePassword(newPassword)
        val isValid = passwordValidation.isAllValid

        _uiState.update {
            it.copy(
                signUpFormState = it.signUpFormState.copy(
                    password = newPassword,
                    isPasswordValid = isValid,
                    passwordValidationState = passwordValidation
                )
            )
        }
    }

    fun onResetPasswordNewPasswordChanged(newPassword: String) {
        val passwordValidation = validatePassword(newPassword)
        val isValid = passwordValidation.isAllValid

        _uiState.update {
            it.copy(
                forgotPasswordForm = it.forgotPasswordForm.copy(
                    newPassword = newPassword,
                    isNewPasswordValid = isValid,
                    passwordValidationState = passwordValidation
                )
            )
        }
    }

    fun onResetPasswordRepeatedNewPasswordChanged(newRepeatedPassword: String) {
        val isValid = validateRepeatedPassword(
            uiState.value.forgotPasswordForm.newPassword,
            newRepeatedPassword
        )
        _uiState.update {
            it.copy(
                forgotPasswordForm = it.forgotPasswordForm.copy(
                    repeatedNewPassword = newRepeatedPassword,
                    isRepeatedNewPasswordValid = isValid,
                    repeatedNewPasswordError = if (isValid || newRepeatedPassword.isEmpty()) null else "Пароли не совпадают"
                )
            )
        }
    }

    private fun validatePassword(password: String): PasswordValidationState {
        return if (password.isEmpty()) {
            PasswordValidationState()
        } else {
            PasswordValidationState(
                hasMinLength = password.length >= 8,
                hasLowercase = password.any { it.isLowerCase() },
                hasUppercase = password.any { it.isUpperCase() },
                hasDigit = password.any { it.isDigit() },
                hasSpecialChar = password.any { !it.isLetterOrDigit() }
            )
        }
    }

    fun onRepeatedPasswordChanged(newPassword: String) {
        val isValid = validateRepeatedPassword(uiState.value.signUpFormState.password, newPassword)
        _uiState.update {
            it.copy(
                signUpFormState = it.signUpFormState.copy(
                    repeatedPassword = newPassword,
                    isRepeatedPasswordValid = isValid,
                    repeatedPasswordError = if (isValid || newPassword.isEmpty()) null else "Пароли не совпадают"
                )
            )
        }
    }

    fun onLoginClick() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, authError = null) }

            try {
                val result = loginUseCase(
                    email = _uiState.value.loginFormState.email,
                    password = _uiState.value.loginFormState.password
                )

                if (result.isSuccess) {
                    _uiState.update { it.copy(isLoginSuccess = true, isLoading = false) }
                } else {
                    _uiState.update {
                        it.copy(
                            loginFormState = it.loginFormState.copy(
                                emailError = "Неверный email или пароль",
                                passwordError = "Неверный email или пароль"
                            ),
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

    fun onForgotPasswordClick() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, authError = null) }

            try {
                delay(1500) // Simulate network delay TODO: Replace with real implementation
                val result = Result.success(Unit)

                if (result.isSuccess) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isContinuedResetPassword = true
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            forgotPasswordForm = it.forgotPasswordForm.copy(
                                emailError = "Некорректный email"
                            ),
                            authError = "Что-то пошло не так при восстановлении пароля",
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

    fun onResetPasswordClick() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, authError = null) }

            try {
                delay(1500) // Simulate network delay TODO: Replace with real implementation
                val result = Result.success(Unit)

                if (result.isSuccess) {
                    _uiState.update {
                        it.copy(
                            forgotPasswordForm = ForgotPasswordFormState(),
                            isLoading = false,
                            isResetPasswordSuccess = true
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            authError = "Что-то пошло не так при сбросе пароля",
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        authError = e.message ?: "Ошибка при сбросе пароля",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onSignUpClick() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, authError = null) }

            try {
                delay(1500) // Simulate network delay TODO: Replace with real implementation
                val result = Result.success(Unit)

                if (result.isSuccess) {
                    _uiState.update {
                        it.copy(
                            signUpFormState = SignUpFormState(),
                            isLoading = false,
                            isContinuedSignUp = true
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            authError = "Что-то пошло не так при регистрации",
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        authError = e.message ?: "Ошибка при регистрации",
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun validateName(newName: String): Boolean = newName.isNotEmpty() && newName.length >= 2

    private fun validateEmail(email: String): Boolean {
        return email.isNotEmpty() &&
                Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun validatePasswordLength(password: String): Boolean {
        return password.length >= 8
    }

    private fun validateRepeatedPassword(password: String, repeatedPassword: String): Boolean {
        return password == repeatedPassword
    }

    fun resetForgotPasswordState() {
        _uiState.update {
            it.copy(isContinuedResetPassword = false)
        }
    }

    fun resetSignUpState() {
        _uiState.update {
            it.copy(isContinuedSignUp = false)
        }
    }

    fun resetResetPasswordState() {
        _uiState.update {
            it.copy(isResetPasswordSuccess = false)
        }
    }
}

