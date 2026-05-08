package com.memowave.app.ui.screen.authentification.components.ui_state

/**
 * State for the email-verification form (OTP entry).
 */
data class VerifyEmailFormState(
    val code: String = "",
    val codeError: String? = null,
)
