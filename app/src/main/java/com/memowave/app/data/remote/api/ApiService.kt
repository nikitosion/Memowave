package com.memowave.app.data.remote.api

import com.memowave.app.data.remote.dto.AuthResponseDto
import com.memowave.app.data.remote.dto.library.CategoryDto
import com.memowave.app.data.remote.dto.library.WordDto
import com.memowave.app.data.remote.dto.user.ChangePasswordDto
import com.memowave.app.data.remote.dto.user.UserDto
import com.memowave.app.data.remote.dto.user.UserLoginReqDto
import com.memowave.app.domain.model.user.UserRegistration
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body request: UserLoginReqDto): Response<AuthResponseDto>

    @POST("auth/register")
    suspend fun register(@Body request: UserRegistration): Response<AuthResponseDto>

    @GET("users/me")
    suspend fun getUserInfo(): Response<UserDto>

    @PUT("users/me")
    suspend fun updateUserInfo(@Body userInfo: UserDto): Response<UserDto>

    @PUT("users/me/change-password")
    suspend fun changePassword(@Body passwordChangeRequest: ChangePasswordDto): Response<Unit>

    @GET("categories")
    suspend fun getUserCategories(): Response<List<CategoryDto>>

    @GET("categories/{categoryId}")
    suspend fun getCategoryById(@Path("categoryId") categoryId: Long): Response<CategoryDto>

    @POST("categories")
    suspend fun addCategory(@Body category: CategoryDto): Response<CategoryDto>

    @PUT("categories/{categoryId}")
    suspend fun updateCategory(@Path("categoryId") categoryId: Long, @Body category: CategoryDto): Response<CategoryDto>

    @DELETE("categories/{categoryId}")
    suspend fun deleteCategory(@Path("categoryId") categoryId: Long): Response<Unit>

    @GET("words")
    suspend fun getUserWords(): Response<List<WordDto>>

    @POST("words")
    suspend fun addWord(@Body word: WordDto): Response<WordDto>

    @PUT("words/{wordId}")
    suspend fun updateWord(@Path("wordId") wordId: Int, @Body word: WordDto): Response<WordDto>

    @DELETE("words/{wordId}")
    suspend fun deleteWord(@Path("wordId") wordId: Int): Response<Unit>
}