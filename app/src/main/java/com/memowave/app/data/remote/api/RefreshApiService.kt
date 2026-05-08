package com.memowave.app.data.remote.api

import com.memowave.app.data.remote.dto.AuthResponseDto
import com.memowave.app.data.remote.dto.RefreshTokenReqDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Dedicated Retrofit interface for the refresh-token endpoint. Lives on its own
 * [com.memowave.app.di.qualifier.RefreshClient]-qualified OkHttp client so the
 * call cannot reenter [com.memowave.app.data.remote.interceptor.TokenAuthenticator]
 * if the refresh itself returns 401.
 */
interface RefreshApiService {
    @POST("auth/refresh")
    suspend fun refresh(@Body body: RefreshTokenReqDto): Response<AuthResponseDto>
}
