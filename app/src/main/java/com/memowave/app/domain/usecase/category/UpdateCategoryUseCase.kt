package com.memowave.app.domain.usecase.category

import com.memowave.app.domain.model.Category
import com.memowave.app.domain.repository.CategoryRepository
import javax.inject.Inject

class UpdateCategoryUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(category: Category): Result<Category> =
        repository.updateCategory(category)
}

