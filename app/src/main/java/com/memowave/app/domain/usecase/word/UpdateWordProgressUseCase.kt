package com.memowave.app.domain.usecase.word

import com.memowave.app.domain.model.Word
import com.memowave.app.domain.repository.WordRepository
import javax.inject.Inject

class UpdateWordProgressUseCase @Inject constructor(
    private val repository: WordRepository
) {
    /**
     * Updates word progress after a flashcard review.
     * @param word The word being reviewed
     * @param quality Rating 0-5 (0 = wrong, 5 = perfect recall)
     * @return Updated word
     *
     * TODO: Implement SM-2 spaced repetition algorithm here:
     *  - Calculate new easeFactor based on quality
     *  - Calculate new interval based on repetitions and easeFactor
     *  - Update repetitions count
     *  - Set nextReviewDate = now + interval days
     */
    suspend operator fun invoke(word: Word, quality: Int): Result<Word> {
        val updatedWord = word.copy(
            quality = quality,
            repetitions = word.repetitions + 1,
            updatedAt = System.currentTimeMillis()
        )
        return repository.updateWord(updatedWord)
    }
}
