package com.memowave.app.ui.screen.authentification.components.ui_state

/**
 * UI state for the [com.memowave.app.ui.screen.authentification.VerifyEmailScreen].
 *
 * @property form Current OTP form state
 * @property isLoading True while the verify-email request is in flight
 * @property resendCooldownSec Seconds remaining until the user may request a new code
 *   (0 means "Resend" is enabled)
 */
data class VerifyEmailUiState(
    val form: VerifyEmailFormState = VerifyEmailFormState(),
    val isLoading: Boolean = false,
    val resendCooldownSec: Int = 0,
)
