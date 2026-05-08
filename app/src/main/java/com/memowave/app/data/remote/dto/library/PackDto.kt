package com.memowave.app.data.remote.dto.library

import kotlinx.serialization.Serializable

@Serializable
data class PackDto(
    val id: Long = 0L,
    val name: String? = null,
    val description: String? = null,
    val language: String? = null
)