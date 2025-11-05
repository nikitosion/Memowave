package com.memowave.app.ui.screen.authentification

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val authError: String? = null,
    val isEmailValid: Boolean = false,
    val isLoginSuccess: Boolean = false,
    val isContinuedResetPassword: Boolean = false,
    val isPasswordValid: Boolean = false
)