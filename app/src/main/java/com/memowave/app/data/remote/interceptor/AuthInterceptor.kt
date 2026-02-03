package com.memowave.app.data.remote.interceptor

import com.memowave.app.core.auth.AuthStateManager
import com.memowave.app.data.local.TokenManager
import jakarta.inject.Inject
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager,
    private val authStateManager: AuthStateManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenManager.getTokenSync()
        val request = chain.request().newBuilder()

        if (!token.isNullOrEmpty()) {
            request.addHeader("Authorization", "Bearer $token")
        }

        val response = chain.proceed(request.build())

        // TODO: Delete 403 once backend is fixed to return 401 for unauthorized requests
        if (response.code == 401 || response.code == 403) {
            runBlocking { tokenManager.clear() }
            authStateManager.setUnauthenticated()
        }

        return response
    }
}