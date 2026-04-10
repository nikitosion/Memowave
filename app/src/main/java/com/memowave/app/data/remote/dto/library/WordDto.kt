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
    val repetitionCount: Int = 0,
    @Serializable(with = LocalDateTimeSerializer::class)
    val nextRepetitionDate: LocalDateTime? = null
)