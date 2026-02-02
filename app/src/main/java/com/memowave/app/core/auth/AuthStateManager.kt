package com.memowave.app.core.auth

import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthStateManager @Inject constructor() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: MutableStateFlow<AuthState> = _authState

    fun setAuthenticated() {
        _authState.value = AuthState.Authenticated
    }

    fun setUnauthenticated() {
        _authState.value = AuthState.Unauthenticated
    }
}

sealed class AuthState {
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
}