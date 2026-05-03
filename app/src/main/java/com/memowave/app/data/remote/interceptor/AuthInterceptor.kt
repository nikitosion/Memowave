package com.memowave.app.data.remote.interceptor

import com.memowave.app.core.auth.AuthState
import com.memowave.app.core.auth.AuthStateManager
import com.memowave.app.data.local.TokenManager
import com.memowave.app.di.ApplicationScope
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager,
    private val authStateManager: AuthStateManager,
    @ApplicationScope private val appScope: CoroutineScope,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenManager.cachedToken.value
        val request = chain.request().newBuilder()

        if (!token.isNullOrEmpty()) {
            request.addHeader("Authorization", "Bearer $token")
        }

        val response = chain.proceed(request.build())

        if (response.code == 401) {
            appScope.launch { tokenManager.clear() }
            authStateManager.setUnauthenticated(AuthState.Unauthenticated.Reason.SessionExpired)
        }

        return response
    }
}
