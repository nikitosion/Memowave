package com.memowave.app.domain.usecase.word

import com.memowave.app.domain.algorithm.FSRS
import com.memowave.app.domain.model.Rating
import com.memowave.app.domain.model.Word
import com.memowave.app.domain.model.WordGrade
import javax.inject.Inject

/**
 * Returns a preview of how each Rating choice would update the given Word.
 * Used to show projected intervals on the rating buttons before the user commits.
 */
class CalculateGradePreviewUseCase @Inject constructor(
    private val fsrs: FSRS
) {
    operator fun invoke(word: Word): Map<Rating, WordGrade> {
        val list = fsrs.calculate(word)
        // FSRS.calculate returns: [0]=Easy, [1]=Good, [2]=Hard, [3]=Again
        return mapOf(
            Rating.Easy to list[0],
            Rating.Good to list[1],
            Rating.Hard to list[2],
            Rating.Again to list[3]
        )
    }
}
