package com.memowave.app.domain.usecase.word

import com.memowave.app.domain.model.Word
import com.memowave.app.domain.repository.WordRepository
import javax.inject.Inject

class GetWordUseCase @Inject constructor(
    private val repository: WordRepository
) {
    suspend operator fun invoke(id: Long): Result<Word?> =
        repository.getWord(id)
}