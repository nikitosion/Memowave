package com.memowave.app.data.remote.api

import com.memowave.app.data.remote.dto.media.UploadImageResponseDto
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface MediaApiService {
    @Multipart
    @POST("images")
    suspend fun uploadImage(@Part file: MultipartBody.Part): Response<UploadImageResponseDto>

    @DELETE("images/{fileName}")
    suspend fun deleteImage(@Path("fileName") fileName: String): Response<Unit>
}
