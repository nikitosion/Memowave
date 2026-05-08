package com.memowave.app.data.remote.interceptor

import com.memowave.app.data.local.TokenManager
import jakarta.inject.Inject
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Attaches the cached access token as `Authorization: Bearer <token>` to every
 * request. 401 recovery is handled by [TokenAuthenticator] — keeping it here
 * too would race the refresh.
 */
class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenManager.cachedAccessToken.value
        val builder = chain.request().newBuilder()
        if (!token.isNullOrEmpty()) {
            builder.addHeader("Authorization", "Bearer $token")
        }
        return chain.proceed(builder.build())
    }
}
