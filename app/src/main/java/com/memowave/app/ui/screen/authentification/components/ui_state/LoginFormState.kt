package com.memowave.app.ui.screen.authentification.components.ui_state

data class LoginFormState(
    val email: String = "",
    val password: String = "",
    val isEmailValid: Boolean = false,
    val isPasswordValid: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
)
