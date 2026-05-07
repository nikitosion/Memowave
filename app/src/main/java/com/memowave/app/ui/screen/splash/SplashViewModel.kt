package com.memowave.app.ui.screen.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memowave.app.R
import com.memowave.app.core.auth.AuthState
import com.memowave.app.core.auth.AuthStateManager
import com.memowave.app.core.util.StringProvider
import com.memowave.app.data.local.TokenManager
import com.memowave.app.domain.usecase.profile.GetUserInfoUseCase
import com.memowave.app.ui.common.notification.NotificationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Bootstrap ViewModel for [SplashScreen].
 *
 * Performs the auto-login probe at app cold start:
 * - If a stored token exists and `getUserInfo` succeeds → mark authenticated,
 *   show a "welcome back" toast, navigate to the main screen.
 * - If a stored token exists but the probe fails AND [TokenAuthenticator] has
 *   already declared the session expired (token refresh exhausted) → navigate
 *   to login. The [com.memowave.app.ui.navigation.NavGraph] auth-state observer
 *   owns the "session expired" notification — Splash does not duplicate it.
 * - If the probe fails but auth state is still Authenticated/Initial (transient
 *   network error, parser glitch) → optimistically authenticate and navigate
 *   to main; the next in-app request will surface a real auth error if any.
 * - If no token is present → silently navigate to login.
 */
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val authStateManager: AuthStateManager,
    private val notificationManager: NotificationManager,
    private val strings: StringProvider,
) : ViewModel() {

    private val _navEvents = Channel<SplashNavEvent>(Channel.BUFFERED)
    val navEvents: Flow<SplashNavEvent> = _navEvents.receiveAsFlow()

    init {
        viewModelScope.launch {
            val storedToken = tokenManager.cachedAccessToken.value
            if (storedToken.isNullOrEmpty()) {
                _navEvents.send(SplashNavEvent.ToLogin)
                return@launch
            }

            val probeResult = runCatching { getUserInfoUseCase() }
                .getOrElse { Result.failure(it) }

            if (probeResult.isSuccess) {
                authStateManager.setAuthenticated()
                notificationManager.showSuccess(
                    strings.getString(R.string.notification_login_success)
                )
                _navEvents.send(SplashNavEvent.ToMain)
                return@launch
            }

            val reason = (authStateManager.authState.value as? AuthState.Unauthenticated)?.reason
            if (reason == AuthState.Unauthenticated.Reason.SessionExpired) {
                // TokenAuthenticator already cleared tokens and flipped auth state.
                // NavGraph observer will navigate to login + show the toast.
                _navEvents.send(SplashNavEvent.ToLogin)
            } else {
                // Transient probe failure — tokens still valid on disk. Optimistic
                // hand-off to main; the next protected call will retry.
                authStateManager.setAuthenticated()
                _navEvents.send(SplashNavEvent.ToMain)
            }
        }
    }
}
