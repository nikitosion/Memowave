package com.memowave.app.data.remote.dto.library

import com.memowave.app.data.remote.serializer.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class WordDto(
    val id: Long? = null,
    val categoryId: Long? = 1,  // используется при отправке (запрос)
    val category: String? = null,  // приходит с сервера (ответ), categoryId в ответе отсутствует
    val text: String,
    val translate: String = "",
    val example: String = "",
    val imageUrl: String? = null,
    val repetitionCount: Int = 0,
    @Serializable(with = LocalDateTimeSerializer::class)
    val nextRepetitionDate: LocalDateTime? = null
)