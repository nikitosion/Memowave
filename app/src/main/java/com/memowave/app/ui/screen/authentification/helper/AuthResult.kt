package com.memowave.app.ui.screen.authentification.helper

/**
 * Unified result type for authentication-related operations.
 *
 * - [Success] — operation completed successfully, optionally carrying a payload of type [T].
 *   For operations without payload use `AuthResult<Unit>` and `Success(Unit)`.
 * - [Failure] — business-level failure (e.g. invalid credentials, user not found),
 *   carries a human-readable [message].
 * - [Error] — system/exception-level error (e.g. network, unexpected),
 *   carries a human-readable [message].
 */
sealed class AuthResult<out T> {
    data class Success<T>(val data: T) : AuthResult<T>()
    data class Failure(val message: String) : AuthResult<Nothing>()
    data class Error(val message: String) : AuthResult<Nothing>()
}
