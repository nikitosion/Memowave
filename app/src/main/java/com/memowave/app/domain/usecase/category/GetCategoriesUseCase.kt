package com.memowave.app.domain.usecase.category

import com.memowave.app.domain.model.Category
import com.memowave.app.domain.repository.CategoryRepository
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(): Result<List<Category>> =
        repository.getCategories()
}