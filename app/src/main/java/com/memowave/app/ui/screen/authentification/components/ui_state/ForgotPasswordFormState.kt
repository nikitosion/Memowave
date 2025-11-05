package com.memowave.app.ui.screen.authentification.components.ui_state

data class ForgotPasswordFormState(
    val email: String = "",
    val isEmailValid: Boolean = false,
    val emailError: String? = null,
)