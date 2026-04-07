package com.memowave.app.data.remote.api

import com.memowave.app.data.remote.dto.AuthResponseDto
import com.memowave.app.data.remote.dto.library.CategoryDto
import com.memowave.app.data.remote.dto.library.WordDto
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
    suspend fun register(@Body request: UserRegistration): Response<Unit>

    @GET("users/user/current")
    suspend fun getUserInfo(): Response<UserDto>

    @GET("categories")
    suspend fun getUserCategories(): Response<List<CategoryDto>>

    @POST("categories/category/new")
    suspend fun addCategory(@Body category: CategoryDto): Response<CategoryDto>

    @PUT("categories/category/{categoryId}/update")
    suspend fun updateCategory(@Path("categoryId") categoryId: Int, @Body category: CategoryDto): Response<CategoryDto>

    @DELETE("categories/category/{categoryId}/delete")
    suspend fun deleteCategory(@Path("categoryId") categoryId: Int): Response<Unit>

    @GET("words")
    suspend fun getUserWords(): Response<List<WordDto>>

    @POST("words/word/add")
    suspend fun addWord(@Body word: WordDto): Response<WordDto>

    @PUT("words/word/{wordId}/update")
    suspend fun updateWord(@Path("wordId") wordId: Int, @Body word: WordDto): Response<WordDto>

    @PUT("words/word/{wordId}/delete")
    suspend fun deleteWord(@Path("wordId") wordId: Int): Response<Unit>
}