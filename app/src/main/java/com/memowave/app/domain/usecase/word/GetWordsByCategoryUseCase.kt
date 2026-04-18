package com.memowave.app.domain.usecase.word

import com.memowave.app.domain.model.Word
import com.memowave.app.domain.repository.WordRepository
import javax.inject.Inject

class GetWordsByCategoryUseCase @Inject constructor(
    private val repository: WordRepository
) {
    suspend operator fun invoke(categoryId: Long): Result<List<Word>> =
        repository.getWordsByCategory(categoryId)
}
