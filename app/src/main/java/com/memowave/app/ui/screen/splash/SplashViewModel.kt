package com.memowave.app.ui.screen.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memowave.app.R
import com.memowave.app.core.auth.AuthState
import com.memowave.app.core.auth.AuthStateManager
import com.memowave.app.core.util.StringProvider
import com.memowave.app.data.local.TokenManager
import com.memowave.app.domain.usecase.auth.LogoutUseCase
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
 * - If a stored token exists but the probe fails → assume token expired,
 *   show a "session expired" toast, clear the session, navigate to login.
 * - If no token is present → silently navigate to login.
 *
 * The Splash destination owns its bootstrap notifications directly. The shared
 * [com.memowave.app.ui.navigation.NavGraph] AuthState observer handles
 * notifications for *subsequent* transitions (manual login / mid-session 401).
 */
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val authStateManager: AuthStateManager,
    private val notificationManager: NotificationManager,
    private val strings: StringProvider,
) : ViewModel() {

    private val _navEvents = Channel<SplashNavEvent>(Channel.BUFFERED)
    val navEvents: Flow<SplashNavEvent> = _navEvents.receiveAsFlow()

    init {
        viewModelScope.launch {
            val storedToken = tokenManager.cachedToken.value
            if (storedToken.isNullOrEmpty()) {
                _navEvents.send(SplashNavEvent.ToLogin)
                return@launch
            }

            val userInfoResult = runCatching { getUserInfoUseCase() }
                .getOrElse { Result.failure(it) }

            if (userInfoResult.isSuccess) {
                authStateManager.setAuthenticated()
                notificationManager.showSuccess(
                    strings.getString(R.string.notification_login_success)
                )
                _navEvents.send(SplashNavEvent.ToMain)
            } else {
                notificationManager.showError(
                    strings.getString(R.string.notification_session_expired)
                )
                runCatching {
                    logoutUseCase(AuthState.Unauthenticated.Reason.SessionExpired)
                }
                _navEvents.send(SplashNavEvent.ToLogin)
            }
        }
    }
}
