package com.memowave.app.domain.usecase.auth

import com.memowave.app.core.auth.AuthState
import com.memowave.app.core.auth.AuthStateManager
import com.memowave.app.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Clears the local session: drops the stored token and marks the user as
 * unauthenticated with the given [reason].
 *
 * Default reason is [AuthState.Unauthenticated.Reason.ManualLogout] — the
 * common case when the user taps "Sign out". The auto-login probe in
 * [com.memowave.app.ui.screen.splash.SplashViewModel] passes
 * [AuthState.Unauthenticated.Reason.SessionExpired] when it detects an expired
 * token at cold start.
 */
class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val authStateManager: AuthStateManager,
) {
    suspend operator fun invoke(
        reason: AuthState.Unauthenticated.Reason = AuthState.Unauthenticated.Reason.ManualLogout,
    ): Result<Unit> {
        authStateManager.setUnauthenticated(reason)
        return authRepository.logout()
    }
}
