package com.memowave.app.data.repository

import androidx.room.withTransaction
import com.memowave.app.data.local.dao.CategoryDao
import com.memowave.app.data.local.dao.SyncQueueDao
import com.memowave.app.data.local.database.MemowaveDatabase
import com.memowave.app.data.local.entity.SyncQueueEntity
import com.memowave.app.data.mapper.CategoryMapper
import com.memowave.app.data.remote.api.ApiService
import com.memowave.app.data.sync.SyncEntityType
import com.memowave.app.data.sync.SyncManager
import com.memowave.app.data.sync.SyncOperationType
import com.memowave.app.domain.model.Category
import com.memowave.app.domain.repository.CategoryRepository
import timber.log.Timber
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao,
    private val syncQueueDao: SyncQueueDao,
    private val apiService: ApiService,
    private val categoryMapper: CategoryMapper,
    private val database: MemowaveDatabase,
    private val syncManager: SyncManager
) : CategoryRepository {

    override suspend fun getCategories(): Result<List<Category>> {
        try {
            syncManager.syncPendingOperations()
        } catch (e: Exception) {
            Timber.w(e, "Failed to sync pending operations before fetching categories")
        }

        try {
            val response = apiService.getUserCategories()
            if (response.isSuccessful && response.body() != null) {
                val dtos = response.body()!!
                val serverEntities = dtos.map { categoryMapper.dtoToEntity(it) }

                categoryDao.upsertCategoriesByRemoteId(serverEntities)

                val serverIds = serverEntities.mapNotNull { it.remoteId }
                categoryDao.deleteSyncedCategoriesNotInIds(serverIds)
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to fetch categories from API, falling back to local cache")
        }

        val entities = categoryDao.getCategories()
        return Result.success(entities.map { categoryMapper.entityToDomain(it) })
    }

    override suspend fun getCategory(id: Long): Result<Category?> {
        return try {
            val entity = categoryDao.getCategoryById(id)
            Result.success(entity?.let { categoryMapper.entityToDomain(it) })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addCategory(category: Category): Result<Category> {
        return try {
            val localId = database.withTransaction {
                val id = categoryDao.insertCategory(
                    categoryMapper.domainToEntity(category).copy(isSynced = false)
                )
                syncQueueDao.insertSyncItem(
                    SyncQueueEntity(
                        entityId = id,
                        entityType = SyncEntityType.CATEGORY,
                        operationType = SyncOperationType.ADD,
                    )
                )
                id
            }
            Result.success(category.copy(id = localId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateCategory(category: Category): Result<Category> {
        return try {
            database.withTransaction {
                val existing = categoryDao.getCategoryById(category.id)
                categoryDao.updateCategory(
                    categoryMapper.domainToEntity(category).copy(
                        isSynced = false,
                        remoteId = existing?.remoteId
                    )
                )
                syncQueueDao.insertSyncItem(
                    SyncQueueEntity(
                        entityId = category.id,
                        entityType = SyncEntityType.CATEGORY,
                        operationType = SyncOperationType.UPDATE,
                    )
                )
            }
            Result.success(category)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteCategory(id: Long): Result<Unit> {
        return try {
            val category = categoryDao.getCategoryById(id)
            if (category == null) {
                Timber.w("Category with id=$id not found for deletion")
                return Result.failure(Exception("Категория не найдена"))
            }
            database.withTransaction {
                categoryDao.deleteCategoryById(id)
                syncQueueDao.insertSyncItem(
                    SyncQueueEntity(
                        entityType = SyncEntityType.CATEGORY,
                        entityId = id,
                        operationType = SyncOperationType.DELETE,
                        remoteId = category.remoteId
                    )
                )
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
