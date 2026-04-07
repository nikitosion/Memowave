package com.memowave.app.data.repository

import com.memowave.app.data.local.dao.CategoryDao
import com.memowave.app.data.local.entity.CategoryEntity
import com.memowave.app.data.mapper.CategoryMapper
import com.memowave.app.data.remote.api.ApiService
import com.memowave.app.data.remote.dto.library.CategoryDto
import com.memowave.app.domain.model.Category
import com.memowave.app.domain.repository.CategoryRepository
import timber.log.Timber
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao,
    private val apiService: ApiService,
    private val categoryMapper: CategoryMapper
) : CategoryRepository {

    override suspend fun getCategories(): Result<List<Category>> {
        return try {
            val response = apiService.getUserCategories()
            if (response.isSuccessful && response.body() != null) {
                val dtos = response.body()!!
                val entities = dtos.map(::dtoToEntity)
                categoryDao.deleteAll()
                categoryDao.insertAll(entities)
            }
            val cached = categoryDao.getCategories()
            if (cached.isNotEmpty()) {
                Result.success(cached.map(::entityToDomain))
            } else {
                Result.failure(Exception("Нет данных о категориях"))
            }
        } catch (e: Exception) {
            val cached = categoryDao.getCategories()
            if (cached.isNotEmpty()) {
                Result.success(cached.map(::entityToDomain))
            } else {
                Result.failure(e)
            }
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
            if (response.isSuccessful && response.body() != null) {
                val domainCategory = categoryMapper.dtoToDomain(response.body()!!)
                categoryDao.insertCategory(domainToEntity(domainCategory))
                Result.success(domainCategory)
            } else {
                val localId = categoryDao.insertCategory(domainToEntity(category))
                val localCategory = category.copy(id = localId)
                Timber.w("API addCategory failed (${response.code()}), saved locally with id=$localId")
                Result.success(localCategory)
            }
        } catch (e: Exception) {
            try {
                val localId = categoryDao.insertCategory(domainToEntity(category))
                val localCategory = category.copy(id = localId)
                Timber.w(e, "API addCategory failed, saved locally with id=$localId")
                Result.success(localCategory)
            } catch (dbError: Exception) {
                Result.failure(dbError)
            }
        }
    }

    override suspend fun updateCategory(category: Category): Result<Category> {
        return try {
            val categoryDto = categoryMapper.domainToDto(category)
            val response = apiService.updateCategory(category.id.toInt(), categoryDto)
            if (response.isSuccessful && response.body() != null) {
                val updatedCategory = categoryMapper.dtoToDomain(response.body()!!)
                categoryDao.insertCategory(domainToEntity(updatedCategory))
                Result.success(updatedCategory)
            } else {
                categoryDao.updateCategory(domainToEntity(category))
                Timber.w("API updateCategory failed (${response.code()}), updated locally")
                Result.success(category)
            }
        } catch (e: Exception) {
            try {
                categoryDao.updateCategory(domainToEntity(category))
                Timber.w(e, "API updateCategory failed, updated locally")
                Result.success(category)
            } catch (dbError: Exception) {
                Result.failure(dbError)
            }
        }
    }

    override suspend fun deleteCategory(id: Long): Result<Unit> {
        return try {
            categoryDao.deleteCategoryById(id)
            try {
                val response = apiService.deleteCategory(id.toInt())
                if (!response.isSuccessful) {
                    Timber.w("API deleteCategory failed (${response.code()}), deleted locally only")
                }
            } catch (e: Exception) {
                Timber.w(e, "API deleteCategory failed, deleted locally only")
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun dtoToEntity(dto: CategoryDto): CategoryEntity {
        return CategoryEntity(
            id = dto.id,
            name = dto.name ?: "",
            description = dto.description,
            colorHex = dto.color,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
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
