package com.memowave.app.data.repository

import androidx.room.withTransaction
import com.memowave.app.data.local.dao.SyncQueueDao
import com.memowave.app.data.local.dao.WordDao
import com.memowave.app.data.local.database.MemowaveDatabase
import com.memowave.app.data.local.entity.SyncQueueEntity
import com.memowave.app.data.mapper.WordMapper
import com.memowave.app.data.remote.api.ApiService
import com.memowave.app.data.sync.SyncEntityType
import com.memowave.app.data.sync.SyncManager
import com.memowave.app.data.sync.SyncOperationType
import com.memowave.app.domain.model.Word
import com.memowave.app.domain.repository.WordRepository
import timber.log.Timber
import javax.inject.Inject

class WordRepositoryImpl @Inject constructor(
    private val wordDao: WordDao,
    private val syncQueueDao: SyncQueueDao,
    private val apiService: ApiService,
    private val wordMapper: WordMapper,
    private val database: MemowaveDatabase,
    private val syncManager: SyncManager
) : WordRepository {

    override suspend fun getWords(): Result<List<Word>> {
        try {
            syncManager.syncPendingOperations()
        } catch (e: Exception) {
            Timber.w(e, "Failed to sync pending operations before fetching words")
        }

        try {
            val response = apiService.getUserWords()
            if (response.isSuccessful && response.body() != null) {
                val serverEntities = response.body()!!.map { wordMapper.dtoToEntity(it) }

                wordDao.upsertWordsByRemoteId(serverEntities)

                val serverIds = serverEntities.mapNotNull { it.remoteId }
                wordDao.deleteSyncedWordsNotInIds(serverIds)
            }
        } catch (e: Exception) {
            Timber.w(e, "Failed to fetch words from API, falling back to local cache")
        }

        val entities = wordDao.getWords()
        return Result.success(entities.map { wordMapper.entityToDomain(it) })
    }

    override suspend fun getWord(id: Long): Result<Word?> {
        return try {
            val entity = wordDao.getWordById(id)
            Result.success(entity?.let { wordMapper.entityToDomain(it) })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addWord(word: Word): Result<Word> {
        return try {
            val localId = database.withTransaction {
                val id = wordDao.insertWord(
                    wordMapper.domainToEntity(word).copy(isSynced = false)
                )
                syncQueueDao.insertSyncItem(
                    SyncQueueEntity(
                        entityId = id,
                        entityType = SyncEntityType.WORD,
                        operationType = SyncOperationType.ADD,
                    )
                )
                id
            }
            Result.success(word.copy(id = localId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateWord(word: Word): Result<Word> {
        return try {
            database.withTransaction {
                val existing = wordDao.getWordById(word.id)
                wordDao.updateWord(
                    wordMapper.domainToEntity(word).copy(
                        isSynced = false,
                        remoteId = existing?.remoteId
                    )
                )
                syncQueueDao.insertSyncItem(
                    SyncQueueEntity(
                        entityId = word.id,
                        entityType = SyncEntityType.WORD,
                        operationType = SyncOperationType.UPDATE,
                    )
                )
            }
            Result.success(word)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteWord(id: Long): Result<Unit> {
        return try {
            val word = wordDao.getWordById(id)
            if (word == null) {
                Timber.w("Word with id=$id not found for deletion")
                return Result.failure(Exception("Слово не найдено"))
            }
            database.withTransaction {
                wordDao.deleteWordById(id)
                syncQueueDao.insertSyncItem(
                    SyncQueueEntity(
                        entityType = SyncEntityType.WORD,
                        entityId = id,
                        operationType = SyncOperationType.DELETE,
                        remoteId = word.remoteId
                    )
                )
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
