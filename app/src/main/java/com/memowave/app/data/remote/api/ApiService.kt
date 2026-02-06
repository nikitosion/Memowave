package com.memowave.app.data.remote.api

import com.memowave.app.data.remote.dto.AuthResponseDto
import com.memowave.app.data.remote.dto.user.UserDto
import com.memowave.app.data.remote.dto.user.UserLoginReqDto
import com.memowave.app.domain.model.user.UserRegistration
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body request: UserLoginReqDto): Response<AuthResponseDto>

    @POST("auth/register")
    suspend fun register(@Body request: UserRegistration): Response<Unit>

    @GET("users/user/current")
    suspend fun getUserInfo(): Response<UserDto>
}