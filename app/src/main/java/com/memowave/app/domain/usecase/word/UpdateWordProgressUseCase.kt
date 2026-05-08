package com.memowave.app.domain.usecase.word

import com.memowave.app.domain.algorithm.CardPhase
import com.memowave.app.domain.algorithm.FSRSFactory
import com.memowave.app.domain.model.Rating
import com.memowave.app.domain.model.Word
import com.memowave.app.domain.repository.WordRepository
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

class UpdateWordProgressUseCase @Inject constructor(
    private val repository: WordRepository,
    private val fsrsFactory: FSRSFactory
) {
    suspend operator fun invoke(word: Word, rating: Rating): Result<Word> {
        val gradeList = fsrsFactory.current().calculate(word)
        // gradeList: [0]=Easy(rating=4), [1]=Good(rating=3), [2]=Hard(rating=2), [3]=Again(rating=1)
        val currentGrade = gradeList[4 - rating.value]

        val newPhase = when {
            rating == Rating.Again -> CardPhase.ReLearning.value
            word.phase == CardPhase.Added.value -> CardPhase.Review.value
            else -> word.phase
        }

        val updatedWord = word.copy(
            stability = currentGrade.stability,
            difficulty = currentGrade.difficulty,
            interval = currentGrade.interval,
            dueDate = addMillisToNow(currentGrade.durationMillis),
            reviewCount = word.reviewCount + 1,
            lastReview = LocalDateTime.now(),
            phase = newPhase
        )

        return repository.updateWord(updatedWord)
    }
}

private fun addMillisToNow(millis: Long): LocalDateTime {
    val nowInstant = Instant.now()
    val newInstant = nowInstant.plusMillis(millis)
    return LocalDateTime.ofInstant(newInstant, ZoneId.systemDefault())
}
