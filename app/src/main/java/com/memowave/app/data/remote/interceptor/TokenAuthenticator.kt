package com.memowave.app.data.remote.interceptor

import com.memowave.app.core.auth.AuthState
import com.memowave.app.core.auth.AuthStateManager
import com.memowave.app.data.local.TokenManager
import com.memowave.app.data.remote.api.RefreshApiService
import com.memowave.app.data.remote.dto.RefreshTokenReqDto
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

/**
 * Owns the recovery path when a request returns 401: calls `/auth/refresh`,
 * persists the rotated pair, and retries the original request with the new
 * access token. On hard failure (refresh itself returns 401, or no refresh
 * token on disk) clears the session and surfaces [AuthState.Unauthenticated.Reason.SessionExpired].
 *
 * Concurrency: a single [refreshLock] serializes refresh attempts process-wide.
 * Rotating refresh would 401 the second concurrent caller otherwise. The
 * double-check inside the lock lets parallel-failed requests piggyback on the
 * single refresh — they retry with the freshly cached access token without
 * issuing another `/auth/refresh`.
 */
@Singleton
class TokenAuthenticator @Inject constructor(
    private val tokenManager: TokenManager,
    private val refreshApiService: RefreshApiService,
    private val authStateManager: AuthStateManager,
) : Authenticator {

    private val refreshLock = Any()

    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= MAX_RETRIES) return giveUp()

        val failedAccess = response.request.header("Authorization")
            ?.removePrefix("Bearer ")

        synchronized(refreshLock) {
            val current = tokenManager.cachedAccessToken.value
            if (!current.isNullOrEmpty() && current != failedAccess) {
                return retryWithToken(response.request, current)
            }

            val refresh = runBlocking { tokenManager.getRefreshToken() }
            if (refresh.isNullOrEmpty()) return giveUp()

            // Any failure to obtain a fresh token pair (network error, 5xx,
            // 401, malformed body) is treated as session expiry: the original
            // request already got 401, so nothing useful can happen without a
            // valid refresh. Surfacing SessionExpired immediately beats
            // leaving the user on a blank screen with stale tokens.
            val refreshResponse = runBlocking {
                runCatching { refreshApiService.refresh(RefreshTokenReqDto(refresh)) }
            }.getOrElse { return giveUp() }

            if (!refreshResponse.isSuccessful) return giveUp()

            val body = refreshResponse.body() ?: return giveUp()

            runBlocking { tokenManager.saveTokens(body.accessToken, body.refreshToken) }
            return retryWithToken(response.request, body.accessToken)
        }
    }

    private fun retryWithToken(original: Request, accessToken: String): Request =
        original.newBuilder()
            .header("Authorization", "Bearer $accessToken")
            .build()

    private fun giveUp(): Request? {
        runBlocking { tokenManager.clear() }
        authStateManager.setUnauthenticated(AuthState.Unauthenticated.Reason.SessionExpired)
        return null
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }

    private companion object {
        const val MAX_RETRIES = 2
    }
}
