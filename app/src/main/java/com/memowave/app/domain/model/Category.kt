package com.memowave.app.domain.model

data class Category(
    val id: Long = 0L,
    val name: String,
    val description: String? = null,
    val colorHex: String? = null,
    val iconRes: Int? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)