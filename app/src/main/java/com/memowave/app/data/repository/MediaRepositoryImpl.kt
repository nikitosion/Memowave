package com.memowave.app.data.repository

import com.memowave.app.data.remote.api.MediaApiService
import com.memowave.app.domain.repository.MediaRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class MediaRepositoryImpl @Inject constructor(
    private val apiService: MediaApiService
) : MediaRepository {

    override suspend fun uploadImage(bytes: ByteArray, mimeType: String): Result<String> {
        return try {
            val media = mimeType.toMediaTypeOrNull()
            val requestBody = bytes.toRequestBody(media, 0, bytes.size)
            val fileName = "image_${System.currentTimeMillis()}.${extensionFromMime(mimeType)}"
            val part = MultipartBody.Part.createFormData(
                name = "file",
                filename = fileName,
                body = requestBody
            )

            val response = apiService.uploadImage(part)
            if (!response.isSuccessful) {
                return Result.failure(Exception("Image upload failed: HTTP ${response.code()}"))
            }
            val body = response.body()
                ?: return Result.failure(Exception("Image upload returned empty body"))
            Result.success(body.fileName)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteImage(fileName: String): Result<Unit> {
        return try {
            val response = apiService.deleteImage(fileName)
            if (response.isSuccessful || response.code() == 404) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Image delete failed: HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun extensionFromMime(mimeType: String): String = when (mimeType.lowercase()) {
        "image/jpeg", "image/jpg" -> "jpg"
        "image/png" -> "png"
        "image/webp" -> "webp"
        "image/gif" -> "gif"
        "image/heic" -> "heic"
        else -> "bin"
    }
}
