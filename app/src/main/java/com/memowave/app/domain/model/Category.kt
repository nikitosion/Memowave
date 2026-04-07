package com.memowave.app.domain.model

import com.memowave.app.data.remote.dto.library.PackDto

data class Category(
    val id: Long = 0L,
    val name: String,
    val description: String? = null,
    val color: String? = null,
    val iconRes: Int? = null,
    val pack: PackDto? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)