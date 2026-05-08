package com.memowave.app.data.mapper

import com.memowave.app.data.local.entity.WordEntity
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
            stability = dto.stability,
            difficulty = dto.difficulty,
            interval = dto.interval,
            dueDate = dto.dueDate ?: LocalDateTime.now(),
            reviewCount = dto.reviewCount,
            lastReview = dto.lastReview,
            phase = dto.phase,
        )
    }

    fun domainToDto(word: Word): WordDto {
        return WordDto(
            categoryId = word.categoryId,
            text = word.original,
            translate = word.translation,
            example = word.examples.firstOrNull() ?: "",
            imageUrl = word.imageUrl,
            stability = word.stability,
            difficulty = word.difficulty,
            interval = word.interval,
            dueDate = word.dueDate,
            reviewCount = word.reviewCount,
            lastReview = word.lastReview,
            phase = word.phase,
        )
    }

    fun entityToDto(entity: WordEntity): WordDto {
        return WordDto(
            categoryId = entity.categoryId,
            text = entity.original,
            translate = entity.translation,
            example = entity.example.firstOrNull() ?: "",
            imageUrl = entity.imageUrl,
            stability = entity.stability,
            difficulty = entity.difficulty,
            interval = entity.interval,
            dueDate = entity.dueDate,
            reviewCount = entity.reviewCount,
            lastReview = entity.lastReview,
            phase = entity.phase,
        )
    }

    fun dtoToEntity(dto: WordDto): WordEntity {
        return WordEntity(
            id = 0L,
            remoteId = dto.id,
            original = dto.text,
            translation = dto.translate,
            categoryId = dto.categoryId,
            example = listOf(dto.example),
            imageUrl = dto.imageUrl,
            isSynced = true,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            stability = dto.stability,
            difficulty = dto.difficulty,
            interval = dto.interval,
            dueDate = dto.dueDate ?: LocalDateTime.now(),
            reviewCount = dto.reviewCount,
            lastReview = dto.lastReview,
            phase = dto.phase,
        )
    }

    fun domainToEntity(domain: Word): WordEntity {
        return WordEntity(
            id = domain.id,
            original = domain.original,
            translation = domain.translation,
            categoryId = domain.categoryId,
            example = domain.examples,
            imageUrl = domain.imageUrl,
            note = domain.note,
            isFavorite = domain.isFavorite,
            isSynced = domain.isSynced,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt,
            stability = domain.stability,
            difficulty = domain.difficulty,
            interval = domain.interval,
            dueDate = domain.dueDate,
            reviewCount = domain.reviewCount,
            lastReview = domain.lastReview,
            phase = domain.phase,
        )
    }

    fun entityToDomain(entity: WordEntity): Word {
        return Word(
            id = entity.id,
            original = entity.original,
            translation = entity.translation,
            categoryId = entity.categoryId,
            examples = entity.example,
            imageUrl = entity.imageUrl,
            note = entity.note,
            isSynced = entity.isSynced,
            isFavorite = entity.isFavorite,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            stability = entity.stability,
            difficulty = entity.difficulty,
            interval = entity.interval,
            dueDate = entity.dueDate,
            reviewCount = entity.reviewCount,
            lastReview = entity.lastReview,
            phase = entity.phase,
        )
    }
}