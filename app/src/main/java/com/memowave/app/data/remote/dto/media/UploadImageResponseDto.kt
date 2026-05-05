package com.memowave.app.data.remote.dto.media

import kotlinx.serialization.Serializable

@Serializable
data class UploadImageResponseDto(
    val fileName: String
)
