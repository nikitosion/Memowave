package com.memowave.app.domain.repository

interface MediaRepository {
    suspend fun uploadImage(bytes: ByteArray, mimeType: String): Result<String>
    suspend fun deleteImage(fileName: String): Result<Unit>
}
