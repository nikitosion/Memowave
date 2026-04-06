package com.memowave.app.domain.repository

import com.memowave.app.domain.model.Category

interface CategoryRepository {
    suspend fun getCategories(): Result<List<Category>>
    suspend fun getCategory(id: Long): Result<Category?>
    suspend fun addCategory(category: Category): Result<Category>
    suspend fun updateCategory(category: Category): Result<Category>
    suspend fun deleteCategory(id: Long): Result<Unit>
}