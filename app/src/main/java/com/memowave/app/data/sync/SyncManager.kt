package com.memowave.app.data.sync

import com.memowave.app.data.local.dao.CategoryDao
import com.memowave.app.data.local.dao.SyncQueueDao
import com.memowave.app.data.local.dao.WordDao
import com.memowave.app.data.local.entity.SyncQueueEntity
import com.memowave.app.data.mapper.CategoryMapper
import com.memowave.app.data.mapper.WordMapper
import com.memowave.app.data.remote.api.ApiService
import timber.log.Timber
import javax.inject.Inject

class SyncManager @Inject constructor(
    private val categoryDao: CategoryDao,
    private val categoryMapper: CategoryMapper,
    private val wordDao: WordDao,
    private val wordMapper: WordMapper,
    private val syncQueueDao: SyncQueueDao,
    private val apiService: ApiService
) {

    suspend fun syncPendingOperations() {
        val pendingOperations = syncQueueDao.getAllSyncItems()

        for (operation in pendingOperations) {
            val success = when (operation.entityType) {
                SyncEntityType.CATEGORY -> syncCategoryOperation(operation)
                SyncEntityType.WORD -> syncWordOperation(operation)
            }

            if (success) {
                syncQueueDao.deleteSyncItemById(operation.id)
            }
        }
    }

    private suspend fun syncCategoryOperation(item: SyncQueueEntity): Boolean {
        return try {
            when (item.operationType) {
                SyncOperationType.ADD -> {
                    val entity = categoryDao.getCategoryById(item.entityId) ?: return true
                    val dto = categoryMapper.entityToDto(entity)
                    val response = apiService.addCategory(dto)
                    when {
                        response.isSuccessful && response.body() != null -> {
                            categoryDao.updateCategory(
                                entity.copy(
                                    remoteId = response.body()!!.id,
                                    isSynced = true
                                )
                            )
                            true
                        }
                        response.code() in 400..499 -> {
                            Timber.e("Sync ADD category failed with ${response.code()}, dropping")
                            true
                        }
                        else -> false
                    }
                }

                SyncOperationType.UPDATE -> {
                    val entity = categoryDao.getCategoryById(item.entityId) ?: return true
                    val remoteId = entity.remoteId ?: return true
                    val dto = categoryMapper.entityToDto(entity)
                    val response = apiService.updateCategory(remoteId, dto)
                    when {
                        response.isSuccessful -> {
                            categoryDao.updateCategory(entity.copy(isSynced = true))
                            true
                        }
                        response.code() in 400..499 -> {
                            Timber.e("Sync UPDATE category failed with ${response.code()}, dropping")
                            true
                        }
                        else -> false
                    }
                }

                SyncOperationType.DELETE -> {
                    val remoteId = item.remoteId ?: return true
                    val response = apiService.deleteCategory(remoteId)
                    when {
                        response.isSuccessful || response.code() == 404 -> true
                        response.code() in 400..499 -> {
                            Timber.e("Sync DELETE category failed with ${response.code()}, dropping")
                            true
                        }
                        else -> false
                    }
                }
            }
        } catch (e: Exception) {
            Timber.w(e, "Network error syncing category ${item.id}")
            false
        }
    }

    private suspend fun syncWordOperation(item: SyncQueueEntity): Boolean {
        return try {
            when (item.operationType) {
                SyncOperationType.ADD -> {
                    val entity = wordDao.getWordById(item.entityId) ?: return true
                    val dto = wordMapper.entityToDto(entity)
                    val response = apiService.addWord(dto)
                    when {
                        response.isSuccessful && response.body() != null -> {
                            wordDao.updateWord(
                                entity.copy(
                                    isSynced = true,
                                    remoteId = response.body()!!.id ?: 0L
                                )
                            )
                            true
                        }
                        response.code() in 400..499 -> {
                            Timber.e("Sync ADD word failed with ${response.code()}, dropping")
                            true
                        }
                        else -> false
                    }
                }

                SyncOperationType.UPDATE -> {
                    val entity = wordDao.getWordById(item.entityId) ?: return true
                    val remoteId = entity.remoteId ?: return true
                    val dto = wordMapper.entityToDto(entity)
                    val response = apiService.updateWord(remoteId.toInt(), dto)
                    when {
                        response.isSuccessful -> {
                            wordDao.updateWord(entity.copy(isSynced = true))
                            true
                        }
                        response.code() in 400..499 -> {
                            Timber.e("Sync UPDATE word failed with ${response.code()}, dropping")
                            true
                        }
                        else -> false
                    }
                }

                SyncOperationType.DELETE -> {
                    val remoteId = item.remoteId ?: return true
                    val response = apiService.deleteWord(remoteId.toInt())
                    when {
                        response.isSuccessful || response.code() == 404 -> true
                        response.code() in 400..499 -> {
                            Timber.e("Sync DELETE word failed with ${response.code()}, dropping")
                            true
                        }
                        else -> false
                    }
                }
            }
        } catch (e: Exception) {
            Timber.w(e, "Network error syncing word ${item.id}")
            false
        }
    }
}
