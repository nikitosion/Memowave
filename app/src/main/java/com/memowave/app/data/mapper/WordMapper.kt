package com.memowave.app.data.mapper

import com.memowave.app.data.remote.dto.library.WordDto
import com.memowave.app.domain.model.Word
import java.time.LocalDateTime

class WordMapper {

    fun dtoToDomain(dto: WordDto): Word {
        return Word(
            id = dto.id ?: 0L,
            categoryId = dto.categoryId,
            original = dto.text,
            translation = dto.translate,
            examples = listOf(dto.example),
            imageUrl = dto.imageUrl,
            repetitions = dto.repetitionCount,
        )
    }

    fun domainToDto(word: Word): WordDto {
        return WordDto(
            categoryId = 1,
            text = word.original,
            translate = word.translation,
            example = word.examples.firstOrNull() ?: "",
            imageUrl = word.imageUrl,
            repetitionCount = word.repetitions,
            nextRepetitionDate = LocalDateTime.now()
        )
    }
}