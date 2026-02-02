package com.memowave.app.ui.screen.authentification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memowave.app.ui.screen.authentification.components.ui_state.AuthUiState
import com.memowave.app.ui.screen.authentification.components.ui_state.ForgotPasswordFormState
import com.memowave.app.ui.screen.authentification.components.ui_state.LoginFormState
import com.memowave.app.ui.screen.authentification.components.ui_state.SignUpFormState
import com.memowave.app.ui.screen.authentification.helper.AuthFormValidator
import com.memowave.app.ui.screen.authentification.helper.AuthOperationHandler
import com.memowave.app.ui.screen.authentification.helper.AuthResult
import com.memowave.app.ui.screen.authentification.helper.GetUserResult
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

/**
 * ViewModel responsible for managing UI state and business logic of authentication screens:
 * - [LoginScreen]
 * - [SignUpScreen]
 * - [ForgotPasswordScreen]
 * - [ResetPasswordScreen]
 *
 * Used by all authentication-related screens in the app.
 *
 * @property formValidator Validates authentication form fields
 * @property operationHandler Handles authentication operations
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val formValidator: AuthFormValidator,      // Form validator: field validation logic
    private val operationHandler: AuthOperationHandler // Operation handler: login, sign up, etc.
) : ViewModel() {

    /**
     * Main authentication UI state, containing all form states and flags.
     */
    private val _uiState = MutableStateFlow(AuthUiState())
    /**
     * StateFlow for observing authentication UI state.
     */
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // ------------------------------ Button enabled states (UI logic) ------------------------------

    /**
     * Indicates if the login button should be enabled.
     *
     * The button is enabled if email and password are valid and loading is not in progress.
     */
    val isLoginButtonEnabled: StateFlow<Boolean> = uiState
        .map {
            with(it.loginFormState) {
                isEmailValid && isPasswordValid
            } && !it.isLoading
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    /**
     * Indicates if the forgot password button should be enabled.
     *
     * The button is enabled if the email is valid and loading is not in progress.
     */
    val isForgotPasswordButtonEnabled: StateFlow<Boolean> = uiState
        .map {
            it.forgotPasswordForm.isEmailValid && !it.isLoading
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    /**
     * Indicates if the reset password button should be enabled.
     *
     * The button is enabled if both password fields are valid and loading is not in progress.
     */
    val isResetPasswordButtonEnabled: StateFlow<Boolean> = uiState
        .map {
            with(it.forgotPasswordForm) {
                isNewPasswordValid && isRepeatedNewPasswordValid
            } && !it.isLoading
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    /**
     * Indicates if the sign up button should be enabled.
     *
     * The button is enabled if all sign up fields are valid and loading is not in progress.
     */
    val isSignUpButtonEnabled: StateFlow<Boolean> = uiState
        .map {
            with(it.signUpFormState) {
                isNameValid && isEmailValid && isPasswordValid && isRepeatedPasswordValid
            } && !it.isLoading
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    init {
        _uiState.update {
            it.copy(
                loginFormState = LoginFormState(
                    email = "root",
                    password = "password123"
                )
            )
        }
        onLoginClick()
    }

    // ---------------------------------- Form field change handlers ----------------------------------

    /**
     * Handles email change in the login form.
     * @param newEmail New email value
     */
    fun onLoginEmailChanged(newEmail: String) {
        _uiState.update {
            it.copy(loginFormState = formValidator.validateLoginEmail(it.loginFormState, newEmail))
        }
    }

    /**
     * Handles password change in the login form.
     * @param newPassword New password value
     */
    fun onLoginPasswordChanged(newPassword: String) {
        _uiState.update {
            it.copy(
                loginFormState = formValidator.validateLoginPassword(
                    it.loginFormState,
                    newPassword
                )
            )
        }
    }

    /**
     * Handles name change in the sign up form.
     * @param newName New username value
     */
    fun onNameChanged(newName: String) {
        _uiState.update {
            it.copy(signUpFormState = formValidator.validateSignUpName(it.signUpFormState, newName))
        }
    }

    /**
     * Handles email change in the sign up form.
     * @param newEmail New email value
     */
    fun onSignUpEmailChanged(newEmail: String) {
        _uiState.update {
            it.copy(
                signUpFormState = formValidator.validateSignUpEmail(
                    it.signUpFormState,
                    newEmail
                )
            )
        }
    }

    /**
     * Handles password change in the sign up form.
     * @param newPassword New password value
     */
    fun onSignUpPasswordChanged(newPassword: String) {
        _uiState.update {
            it.copy(
                signUpFormState = formValidator.validateSignUpPassword(
                    it.signUpFormState,
                    newPassword
                )
            )
        }
    }

    /**
     * Handles repeated password change in the sign up form.
     * @param newPassword New repeated password value
     */
    fun onRepeatedPasswordChanged(newPassword: String) {
        _uiState.update {
            it.copy(
                signUpFormState = formValidator.validateSignUpRepeatedPassword(
                    it.signUpFormState,
                    newPassword
                )
            )
        }
    }

    /**
     * Handles email change in the forgot password form.
     * @param newEmail New email value
     */
    fun onForgotPasswordEmailChanged(newEmail: String) {
        _uiState.update {
            it.copy(
                forgotPasswordForm = formValidator.validateForgotPasswordEmail(
                    it.forgotPasswordForm,
                    newEmail
                )
            )
        }
    }

    /**
     * Handles new password change in the reset password form.
     * @param newPassword New password value
     */
    fun onResetPasswordNewPasswordChanged(newPassword: String) {
        _uiState.update {
            it.copy(
                forgotPasswordForm = formValidator.validateResetNewPassword(
                    it.forgotPasswordForm,
                    newPassword
                )
            )
        }
    }

    /**
     * Handles repeated new password change in the reset password form.
     * @param newRepeatedPassword New repeated password value
     */
    fun onResetPasswordRepeatedNewPasswordChanged(newRepeatedPassword: String) {
        _uiState.update {
            it.copy(
                forgotPasswordForm = formValidator.validateResetRepeatedPassword(
                    it.forgotPasswordForm,
                    newRepeatedPassword
                )
            )
        }
    }

    // -------------- Main authentication operations (login, sign up, password recovery) --------------

    /**
     * Handles login button click. Performs login operation and updates UI state accordingly.
     */
    fun onLoginClick() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, authError = null) }

            when (val result = operationHandler.performLogin(
                email = _uiState.value.loginFormState.email,
                password = _uiState.value.loginFormState.password
            )) {
                is AuthResult.Success -> {
                    _uiState.update { it.copy(isLoginSuccess = true, isLoading = false) }
                }

                is AuthResult.Failure -> {
                    _uiState.update {
                        it.copy(
                            loginFormState = it.loginFormState.copy(
                                emailError = result.message,
                                passwordError = result.message
                            ),
                            authError = result.message,
                            isLoading = false
                        )
                    }
                }

                is AuthResult.Error -> {
                    _uiState.update {
                        it.copy(
                            authError = result.message,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    /**
     * Handles forgot password button click. Performs user lookup by email and updates UI state.
     */
    fun onForgotPasswordClick() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, authError = null) }

            when (val result = operationHandler.performGetUserByEmail(
                email = uiState.value.forgotPasswordForm.email
            )) {
                is GetUserResult.Success -> {
                    _uiState.update {
                        it.copy(
                            userId = result.userId,
                            isLoading = false,
                            isContinuedResetPassword = true
                        )
                    }
                }

                is GetUserResult.Failure -> {
                    _uiState.update {
                        it.copy(
                            forgotPasswordForm = it.forgotPasswordForm.copy(
                                emailError = result.message
                            ),
                            authError = result.message,
                            isLoading = false
                        )
                    }
                }

                is GetUserResult.Error -> {
                    _uiState.update {
                        it.copy(
                            authError = result.message,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    /**
     * Handles reset password button click. Performs password reset and updates UI state.
     */
    fun onResetPasswordClick() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, authError = null) }

            val userId = uiState.value.userId
            if (userId == null) {
                _uiState.update {
                    it.copy(
                        authError = "Error: user not found",
                        isLoading = false
                    )
                }
                return@launch
            }

            when (val result = operationHandler.performResetPassword(
                userId = userId,
                newPassword = uiState.value.forgotPasswordForm.newPassword
            )) {
                is AuthResult.Success -> {
                    _uiState.update {
                        it.copy(
                            forgotPasswordForm = ForgotPasswordFormState(),
                            isLoading = false,
                            isResetPasswordSuccess = true
                        )
                    }
                }

                is AuthResult.Failure -> {
                    _uiState.update {
                        it.copy(
                            authError = result.message,
                            isLoading = false
                        )
                    }
                }

                is AuthResult.Error -> {
                    _uiState.update {
                        it.copy(
                            authError = result.message,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    /**
     * Handles sign up button click. Performs sign up operation and updates UI state.
     */
    fun onSignUpClick() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, authError = null) }

            when (val result = operationHandler.performSignUp(
                email = _uiState.value.signUpFormState.email,
                password = _uiState.value.signUpFormState.password,
                username = _uiState.value.signUpFormState.username
            )) {
                is AuthResult.Success -> {
                    _uiState.update {
                        it.copy(
                            signUpFormState = SignUpFormState(),
                            isLoading = false,
                            isContinuedSignUp = true
                        )
                    }
                }

                is AuthResult.Failure -> {
                    _uiState.update {
                        it.copy(
                            authError = result.message,
                            isLoading = false
                        )
                    }
                }

                is AuthResult.Error -> {
                    _uiState.update {
                        it.copy(
                            authError = result.message,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    // ------------------- Reset temporary state flags (for navigation/form reset) -------------------

    /**
     * Resets the state for continuing to reset password (after successful user search).
     */
    fun resetForgotPasswordState() {
        _uiState.update {
            it.copy(isContinuedResetPassword = false)
        }
    }

    /**
     * Resets the state for continuing sign up (after successful registration).
     */
    fun resetSignUpState() {
        _uiState.update {
            it.copy(isContinuedSignUp = false)
        }
    }

    /**
     * Resets the state for successful password reset (after successful reset).
     */
    fun resetResetPasswordState() {
        _uiState.update {
            it.copy(isResetPasswordSuccess = false)
        }
    }
}
