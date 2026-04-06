package com.memowave.app.domain.usecase.word

import com.memowave.app.domain.repository.WordRepository
import javax.inject.Inject

class DeleteWordUseCase @Inject constructor(
    private val repository: WordRepository
) {
    suspend operator fun invoke(id: Long): Result<Unit> =
        repository.deleteWord(id)
}