package com.memowave.app.data.mapper

import com.memowave.app.data.remote.dto.library.CategoryDto
import com.memowave.app.data.remote.dto.library.PackDto
import com.memowave.app.domain.model.Category

class CategoryMapper {

    fun dtoToDomain(dto: CategoryDto): Category {
        return Category(
            id = dto.id,
            name = dto.name ?: "",
            description = dto.description ?: "",
            color = dto.color ?: "#FFFFFF"
        )
    }

    fun domainToDto(category: Category): CategoryDto {
        return CategoryDto(
            id = category.id,
            name = category.name,
            description = category.description,
            color = category.color
        )
    }
}