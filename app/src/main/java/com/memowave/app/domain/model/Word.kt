package com.memowave.app.domain.model

data class Word(
    val id: Long = 0L,
    val original: String,
    val translation: String,
    val categoryId: Long? = null,
    val examples: List<String> = emptyList(),
    val imageUrl: String? = null,
    val audioUrl: String? = null,
    val note: String? = null,
    // Spaced Repetition System (SRS) fields
    val quality: Int = 0,
    val repetitions: Int = 0,
    val prevEaseFactor: Double = 2.5,
    val prevInterval: Int = 0,
    // -------------------------------------
    val isSynced: Boolean = false,
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
