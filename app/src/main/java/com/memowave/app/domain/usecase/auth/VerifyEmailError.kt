package com.memowave.app.domain.usecase.auth

sealed class VerifyEmailError(message: String) : Exception(message) {
    object InvalidCode : VerifyEmailError("Invalid or expired code")
    object Forbidden : VerifyEmailError("Action not allowed")
    object SendFailed : VerifyEmailError("Failed to send verification code")
    object Unknown : VerifyEmailError("Unknown error")
}
