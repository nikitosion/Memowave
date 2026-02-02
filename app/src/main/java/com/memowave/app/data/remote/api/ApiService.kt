package com.memowave.app.data.remote.api

import com.memowave.app.data.remote.dto.AuthResponseDto
import com.memowave.app.data.remote.dto.user.UserDto
import com.memowave.app.data.remote.dto.user.UserLoginDto
import com.memowave.app.data.remote.dto.user.UserRegisterDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body request: UserLoginDto): Response<AuthResponseDto>

    @POST("auth/register")
    suspend fun register(@Body request: UserRegisterDto): Response<UserDto>

    @GET("users/{id}/user")
    suspend fun getUserProfileInfoById(@Path("id") userId: Long): Response<UserDto>
}