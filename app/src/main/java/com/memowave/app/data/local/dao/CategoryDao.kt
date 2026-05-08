package com.memowave.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.memowave.app.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Query("SELECT * FROM categories ORDER BY name COLLATE NOCASE ASC")
    fun observeCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories ORDER BY name COLLATE NOCASE ASC")
    suspend fun getCategories(): List<CategoryEntity>

    @Query("SELECT * FROM categories WHERE id = :id LIMIT 1")
    suspend fun getCategoryById(id: Long): CategoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity): Long

    @Query("SELECT * FROM categories WHERE remoteId = :remoteId LIMIT 1")
    suspend fun getCategoryByRemoteId(remoteId: Long): CategoryEntity?

    @Transaction
    suspend fun upsertCategoriesByRemoteId(categories: List<CategoryEntity>) {
        for (category in categories) {
            val existing = getCategoryByRemoteId(category.remoteId!!)
            if (existing != null) {
                if (existing.isSynced) {
                    updateCategory(category.copy(id = existing.id, isSynced = true))
                }
            } else {
                insertCategory(category)
            }
        }
    }

    @Update
    suspend fun updateCategory(category: CategoryEntity)

    @Delete
    suspend fun deleteCategory(category: CategoryEntity)

    @Query("DELETE FROM categories WHERE isSynced = 1 AND remoteId NOT IN (:ids)")
    suspend fun deleteSyncedCategoriesNotInIds(ids: List<Long>)

    @Query("DELETE FROM categories WHERE id = :id")
    suspend fun deleteCategoryById(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<CategoryEntity>)

    @Query("DELETE FROM categories")
    suspend fun deleteAll()
}