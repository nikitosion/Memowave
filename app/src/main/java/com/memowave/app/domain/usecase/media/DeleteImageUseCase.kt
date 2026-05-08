package com.memowave.app.domain.usecase.media

import com.memowave.app.domain.repository.MediaRepository
import javax.inject.Inject

class DeleteImageUseCase @Inject constructor(
    private val repository: MediaRepository
) {
    suspend operator fun invoke(fileName: String): Result<Unit> =
        repository.deleteImage(fileName)
}
