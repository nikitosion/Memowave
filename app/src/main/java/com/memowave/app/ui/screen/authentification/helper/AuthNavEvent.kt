package com.memowave.app.ui.screen.authentification.helper

/**
 * One-shot navigation events emitted by the auth ViewModels after operations complete.
 * Consumed by their owning screens to trigger navigation exactly once per occurrence
 * (in contrast to boolean flags in UI state, which require manual reset and may re-fire
 * on recomposition).
 */
sealed interface AuthNavEvent {
    /** Successful login or auto-login on app start. Navigate to the main page. */
    data object ToMain : AuthNavEvent

    /** Forgot-password flow: user located by email, navigate to the reset-password step. */
    data class ToResetPassword(val userId: Long) : AuthNavEvent

    /**
     * Sign-up or forgot-password flow: code has been dispatched to the user's email,
     * navigate to the OTP-entry screen. [flow] decides where to go on successful verification.
     */
    data class ToVerifyEmail(val userId: Long, val flow: VerifyEmailFlow) : AuthNavEvent

    /** Sign-up or password reset succeeded. Return to the login screen. */
    data object BackToLogin : AuthNavEvent
}

/**
 * Caller of the [com.memowave.app.ui.screen.authentification.VerifyEmailScreen].
 * Drives the post-verification destination:
 *  - [SignUp] → main page (registration is now complete)
 *  - [ForgotPassword] → reset-password screen (set the new password)
 */
enum class VerifyEmailFlow {
    SignUp,
    ForgotPassword,
}

