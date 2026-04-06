package com.memowave.app.data.repository

import com.memowave.app.data.local.dao.WordDao
import com.memowave.app.data.local.entity.WordEntity
import com.memowave.app.domain.model.Word
import com.memowave.app.domain.repository.WordRepository
import javax.inject.Inject

class WordRepositoryImpl @Inject constructor(
    private val wordDao: WordDao
) : WordRepository {

    override suspend fun getWords(): Result<List<Word>> {
        return try {
            val entities = wordDao.getWords()
            Result.success(entities.map(::entityToDomain))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getWord(id: Long): Result<Word?> {
        return try {
            val entity = wordDao.getWordById(id)
            Result.success(entity?.let(::entityToDomain))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addWord(word: Word): Result<Word> {
        return try {
            val now = System.currentTimeMillis()
            val entity = domainToEntity(
                word.copy(
                    createdAt = now,
                    updatedAt = now
                )
            )
            val id = wordDao.insertWord(entity)
            val saved = entity.copy(id = id)
            Result.success(entityToDomain(saved))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateWord(word: Word): Result<Word> {
        return try {
            val entity = domainToEntity(
                word.copy(
                    updatedAt = System.currentTimeMillis()
                )
            )
            wordDao.updateWord(entity)
            Result.success(word.copy(updatedAt = entity.updatedAt))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteWord(id: Long): Result<Unit> {
        return try {
            wordDao.deleteWordById(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun entityToDomain(entity: WordEntity): Word {
        return Word(
            id = entity.id,
            original = entity.original,
            translation = entity.translation,
            categoryId = entity.categoryId,
            examples = entity.example,
            note = entity.note,
            isFavorite = entity.isFavorite,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    private fun domainToEntity(domain: Word): WordEntity {
        return WordEntity(
            id = domain.id,
            original = domain.original,
            translation = domain.translation,
            categoryId = domain.categoryId,
            example = domain.examples,
            note = domain.note,
            isFavorite = domain.isFavorite,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }
}