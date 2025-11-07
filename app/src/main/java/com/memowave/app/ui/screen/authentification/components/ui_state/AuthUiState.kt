package com.memowave.app.ui.screen.authentification.components.ui_state

data class AuthUiState(
    val userId: Long? = null,
    val signUpFormState: SignUpFormState = SignUpFormState(),
    val loginFormState: LoginFormState = LoginFormState(),
    val forgotPasswordForm: ForgotPasswordFormState = ForgotPasswordFormState(),
    val isLoading: Boolean = false,
    val authError: String? = null,
    val isLoginSuccess: Boolean = false,
    val isContinuedResetPassword: Boolean = false,
    val isResetPasswordSuccess: Boolean = false,
    val isContinuedSignUp: Boolean = false,
)