package com.memowave.app.core.auth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Process-wide authentication state. Single source of truth for whether the user
 * is currently authenticated and, when not, why.
 */
@Singleton
class AuthStateManager @Inject constructor() {
    private val _authState = MutableStateFlow<AuthState>(
        AuthState.Unauthenticated(AuthState.Unauthenticated.Reason.Initial)
    )
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun setAuthenticated() {
        _authState.value = AuthState.Authenticated
    }

    fun setUnauthenticated(reason: AuthState.Unauthenticated.Reason) {
        _authState.value = AuthState.Unauthenticated(reason)
    }
}

sealed class AuthState {
    data object Authenticated : AuthState()

    /**
     * The user has no active session. The [reason] explains why so the UI can
     * differentiate between an initial cold start, a manual logout, and a
     * server-rejected session.
     */
    data class Unauthenticated(val reason: Reason) : AuthState() {
        enum class Reason {
            /** App just started and the bootstrap probe hasn't decided yet. */
            Initial,

            /** User explicitly logged out via the UI. */
            ManualLogout,

            /** Server rejected the token (401) — session expired or revoked. */
            SessionExpired,
        }
    }
}
