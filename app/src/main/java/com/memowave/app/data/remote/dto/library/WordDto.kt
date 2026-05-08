package com.memowave.app.data.remote.dto.library

import com.memowave.app.data.remote.serializer.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class WordDto(
    val id: Long? = null,
    val categoryId: Long? = null,
    val text: String,
    val translate: String = "",
    val example: String = "",
    val imageUrl: String? = null,
    val stability: Double = 2.5,
    val difficulty: Double = 2.5,
    val interval: Int = 0,
    @Serializable(with = LocalDateTimeSerializer::class)
    val dueDate: LocalDateTime? = null,
    val reviewCount: Int = 0,
    @Serializable(with = LocalDateTimeSerializer::class)
    val lastReview: LocalDateTime? = null,
    val phase: Int = 0,
)