package com.memowave.app.data.repository

import com.memowave.app.data.local.dao.WordDao
import com.memowave.app.data.local.entity.WordEntity
import com.memowave.app.data.mapper.WordMapper
import com.memowave.app.data.remote.api.ApiService
import com.memowave.app.domain.model.Word
import com.memowave.app.domain.repository.WordRepository
import javax.inject.Inject

class WordRepositoryImpl @Inject constructor(
    private val wordDao: WordDao,
    private val apiService: ApiService,
    private val wordMapper: WordMapper
) : WordRepository {

    override suspend fun getWords(): Result<List<Word>> {
        return try {
            val words = apiService.getUserWords().body()
                ?: return Result.failure(Exception("Не удалось загрузить слова с сервера"))

            val domainWords = words.map {
                word -> wordMapper.dtoToDomain(word)
            }
            Result.success(domainWords)
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
            val wordDto = wordMapper.domainToDto(word)
            val response = apiService.addWord(wordDto)
            if (!response.isSuccessful || response.body() == null) {
                return Result.failure(Exception("Ошибка при добавлении слова: ${response.code()}"))
            }
            val domainAddedWord = wordMapper.dtoToDomain(response.body()!!)
            Result.success(domainAddedWord)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateWord(word: Word): Result<Word> {
        return try {
            val wordDto = wordMapper.domainToDto(word)
            val response = apiService.updateWord(word.id.toInt(), wordDto)
            if (!response.isSuccessful || response.body() == null) {
                return Result.failure(Exception("Ошибка при обновлении слова: ${response.code()}"))
            }
            val domainUpdatedWord = wordMapper.dtoToDomain(response.body()!!)
            Result.success(domainUpdatedWord)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteWord(id: Long): Result<Unit> {
        return try {
            val response = apiService.deleteWord(id.toInt())
            if (!response.isSuccessful) {
                return Result.failure(Exception("Ошибка при удалении слова: ${response.code()}"))
            }
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