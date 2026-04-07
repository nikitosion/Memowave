package com.memowave.app.data.remote.dto.library

import kotlinx.serialization.Serializable

@Serializable
data class CategoryDto(
    val id: Long,
    val name: String? = null,
    val description: String? = null,
    val color: String? = null,
    val userId: Long = 0L
)