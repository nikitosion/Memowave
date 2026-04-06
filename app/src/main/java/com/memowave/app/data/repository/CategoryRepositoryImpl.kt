package com.memowave.app.data.repository

import com.memowave.app.data.local.dao.CategoryDao
import com.memowave.app.data.local.entity.CategoryEntity
import com.memowave.app.domain.model.Category
import com.memowave.app.domain.repository.CategoryRepository
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao
) : CategoryRepository {

    override suspend fun getCategories(): Result<List<Category>> {
        return try {
            val entities = categoryDao.getCategories()
            Result.success(entities.map(::entityToDomain))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCategory(id: Long): Result<Category?> {
        return try {
            val entity = categoryDao.getCategoryById(id)
            Result.success(entity?.let(::entityToDomain))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addCategory(category: Category): Result<Category> {
        return try {
            val now = System.currentTimeMillis()
            val entity = domainToEntity(
                category.copy(
                    createdAt = now,
                    updatedAt = now
                )
            )
            val id = categoryDao.insertCategory(entity)
            val saved = entity.copy(id = id)
            Result.success(entityToDomain(saved))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateCategory(category: Category): Result<Category> {
        return try {
            val entity = domainToEntity(
                category.copy(
                    updatedAt = System.currentTimeMillis()
                )
            )
            categoryDao.updateCategory(entity)
            Result.success(category.copy(updatedAt = entity.updatedAt))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteCategory(id: Long): Result<Unit> {
        return try {
            categoryDao.deleteCategoryById(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun entityToDomain(entity: CategoryEntity): Category {
        return Category(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            colorHex = entity.colorHex,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    private fun domainToEntity(domain: Category): CategoryEntity {
        return CategoryEntity(
            id = domain.id,
            name = domain.name,
            description = domain.description,
            colorHex = domain.colorHex,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }
}