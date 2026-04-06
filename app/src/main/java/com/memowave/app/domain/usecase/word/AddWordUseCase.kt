package com.memowave.app.domain.usecase.word

import com.memowave.app.domain.model.Word
import com.memowave.app.domain.repository.WordRepository
import javax.inject.Inject

class AddWordUseCase @Inject constructor(
    private val repository: WordRepository
) {
    suspend operator fun invoke(word: Word): Result<Word> =
        repository.addWord(word)
}