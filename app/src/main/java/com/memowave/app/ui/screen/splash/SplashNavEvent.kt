package com.memowave.app.ui.screen.splash

/**
 * One-shot navigation events emitted by [SplashViewModel] after the auto-login
 * probe completes. Consumed by [SplashScreen] to navigate exactly once.
 */
sealed interface SplashNavEvent {
    /** Token was valid and user info was fetched — proceed to the main screen. */
    data object ToMain : SplashNavEvent

    /** No valid session — proceed to the login screen. */
    data object ToLogin : SplashNavEvent
}
