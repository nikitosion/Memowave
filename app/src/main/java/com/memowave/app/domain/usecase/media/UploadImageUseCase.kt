package com.memowave.app.domain.usecase.media

import com.memowave.app.domain.repository.MediaRepository
import javax.inject.Inject

class UploadImageUseCase @Inject constructor(
    private val repository: MediaRepository
) {
    suspend operator fun invoke(bytes: ByteArray, mimeType: String): Result<String> =
        repository.uploadImage(bytes, mimeType)
}
