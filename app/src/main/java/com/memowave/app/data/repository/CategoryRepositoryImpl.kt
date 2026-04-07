package com.memowave.app.data.repository

import com.memowave.app.data.local.dao.CategoryDao
import com.memowave.app.data.local.entity.CategoryEntity
import com.memowave.app.data.mapper.CategoryMapper
import com.memowave.app.data.remote.api.ApiService
import com.memowave.app.domain.model.Category
import com.memowave.app.domain.repository.CategoryRepository
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao,
    private val apiService: ApiService,
    private val categoryMapper: CategoryMapper
) : CategoryRepository {

    override suspend fun getCategories(): Result<List<Category>> {
        return try {
            val categories = apiService.getUserCategories().body()
                ?: return Result.failure(Exception("Не удалось загрузить категории с сервера"))
            val domainCategories = categories.map { category -> categoryMapper.dtoToDomain(category) }
            Result.success(domainCategories)
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
            val categoryDto = categoryMapper.domainToDto(category)
            val response = apiService.addCategory(categoryDto)
            if (!response.isSuccessful || response.body() == null) {
                return Result.failure(Exception("Ошибка при добавлении категории: ${response.code()}"))
            }
            val domainCategory = categoryMapper.dtoToDomain(response.body()!!)
            Result.success(domainCategory)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateCategory(category: Category): Result<Category> {
        return try {
            val categoryDto = categoryMapper.domainToDto(category)
            val response = apiService.updateCategory(category.id.toInt(), categoryDto)
            if (!response.isSuccessful || response.body() == null) {
                return Result.failure(Exception("Ошибка при обновлении категории: ${response.code()}"))
            }
            val updatedCategory = categoryMapper.dtoToDomain(response.body()!!)
            Result.success(updatedCategory)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteCategory(id: Long): Result<Unit> {
        return try {
            val response = apiService.deleteCategory(id.toInt())
            if (!response.isSuccessful) {
                return Result.failure(Exception("Ошибка при удалении категории: ${response.code()}"))
            }
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
            color = entity.colorHex,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    private fun domainToEntity(domain: Category): CategoryEntity {
        return CategoryEntity(
            id = domain.id,
            name = domain.name,
            description = domain.description,
            colorHex = domain.color,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }
}