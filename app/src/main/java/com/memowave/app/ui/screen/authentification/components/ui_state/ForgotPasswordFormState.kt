package com.memowave.app.ui.screen.authentification.components.ui_state

/**
 * State for the forgot-password form (email-lookup step).
 */
data class ForgotPasswordFormState(
    val email: String = "",
    val isEmailValid: Boolean = false,
    val emailError: String? = null,
)
