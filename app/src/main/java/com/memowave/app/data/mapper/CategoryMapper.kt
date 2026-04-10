package com.memowave.app.data.mapper

import com.memowave.app.data.local.entity.CategoryEntity
import com.memowave.app.data.remote.dto.library.CategoryDto
import com.memowave.app.data.remote.dto.library.PackDto
import com.memowave.app.domain.model.Category

class CategoryMapper {

    fun entityToDto(entity: CategoryEntity): CategoryDto {
        return CategoryDto(
            id = entity.remoteId ?: 0L,
            name = entity.name,
            description = entity.description,
            color = entity.colorHex
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

    fun domainToEntity(domain: Category): CategoryEntity {
        return CategoryEntity(
            id = domain.id,
            name = domain.name,
            description = domain.description,
            colorHex = domain.color,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }

    fun dtoToEntity(dto: CategoryDto): CategoryEntity {
        return CategoryEntity(
            id = 0L,
            name = dto.name ?: "",
            description = dto.description,
            colorHex = dto.color,
            isSynced = true,
            remoteId = dto.id,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
    }

    fun dtoToDomain(dto: CategoryDto): Category {
        return Category(
            id = dto.id,
            name = dto.name ?: "",
            description = dto.description ?: "",
            color = dto.color ?: "#FFFFFF"
        )
    }

    fun entityToDomain(entity: CategoryEntity): Category {
        return Category(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            color = entity.colorHex,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
}
