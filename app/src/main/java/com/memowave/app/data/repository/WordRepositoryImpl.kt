package com.memowave.app.data.repository

import com.memowave.app.data.local.dao.WordDao
import com.memowave.app.data.local.entity.WordEntity
import com.memowave.app.data.mapper.WordMapper
import com.memowave.app.data.remote.api.ApiService
import com.memowave.app.data.remote.dto.library.WordDto
import com.memowave.app.domain.model.Word
import com.memowave.app.domain.repository.WordRepository
import timber.log.Timber
import javax.inject.Inject

class WordRepositoryImpl @Inject constructor(
    private val wordDao: WordDao,
    private val apiService: ApiService,
    private val wordMapper: WordMapper
) : WordRepository {

    override suspend fun getWords(): Result<List<Word>> {
        return try {
            val response = apiService.getUserWords()
            if (response.isSuccessful && response.body() != null) {
                val dtos = response.body()!!
                val entities = dtos.map(::dtoToEntity)
                wordDao.deleteAll()
                wordDao.insertAll(entities)
            }
            val cached = wordDao.getWords()
            if (cached.isNotEmpty()) {
                Result.success(cached.map(::entityToDomain))
            } else {
                Result.failure(Exception("Нет данных о словах"))
            }
        } catch (e: Exception) {
            val cached = wordDao.getWords()
            if (cached.isNotEmpty()) {
                Result.success(cached.map(::entityToDomain))
            } else {
                Result.failure(e)
            }
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
            if (response.isSuccessful && response.body() != null) {
                val domainWord = wordMapper.dtoToDomain(response.body()!!)
                wordDao.insertWord(domainToEntity(domainWord))
                Result.success(domainWord)
            } else {
                val localId = wordDao.insertWord(domainToEntity(word))
                val localWord = word.copy(id = localId)
                Timber.w("API addWord failed (${response.code()}), saved locally with id=$localId")
                Result.success(localWord)
            }
        } catch (e: Exception) {
            try {
                val localId = wordDao.insertWord(domainToEntity(word))
                val localWord = word.copy(id = localId)
                Timber.w(e, "API addWord failed, saved locally with id=$localId")
                Result.success(localWord)
            } catch (dbError: Exception) {
                Result.failure(dbError)
            }
        }
    }

    override suspend fun updateWord(word: Word): Result<Word> {
        return try {
            val wordDto = wordMapper.domainToDto(word)
            val response = apiService.updateWord(word.id.toInt(), wordDto)
            if (response.isSuccessful && response.body() != null) {
                val updatedWord = wordMapper.dtoToDomain(response.body()!!)
                wordDao.insertWord(domainToEntity(updatedWord))
                Result.success(updatedWord)
            } else {
                wordDao.updateWord(domainToEntity(word))
                Timber.w("API updateWord failed (${response.code()}), updated locally")
                Result.success(word)
            }
        } catch (e: Exception) {
            try {
                wordDao.updateWord(domainToEntity(word))
                Timber.w(e, "API updateWord failed, updated locally")
                Result.success(word)
            } catch (dbError: Exception) {
                Result.failure(dbError)
            }
        }
    }

    override suspend fun deleteWord(id: Long): Result<Unit> {
        return try {
            wordDao.deleteWordById(id)
            try {
                val response = apiService.deleteWord(id.toInt())
                if (!response.isSuccessful) {
                    Timber.w("API deleteWord failed (${response.code()}), deleted locally only")
                }
            } catch (e: Exception) {
                Timber.w(e, "API deleteWord failed, deleted locally only")
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun dtoToEntity(dto: WordDto): WordEntity {
        return WordEntity(
            id = dto.id ?: 0L,
            original = dto.text,
            translation = dto.translate,
            categoryId = dto.categoryId,
            example = listOf(dto.example),
            note = null,
            isFavorite = false,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
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
