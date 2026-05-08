package com.memowave.app.domain.model

import java.time.LocalDateTime

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
    val stability: Double = 2.5,
    val difficulty: Double = 2.5,
    val interval: Int = 0,
    val dueDate: LocalDateTime = LocalDateTime.now(),
    val reviewCount: Int = 0,
    val lastReview: LocalDateTime? = null,
    val phase: Int = 0,
    // -------------------------------------
    val isSynced: Boolean = false,
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
