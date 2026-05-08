package com.memowave.app.data.remote.dto.security

import kotlinx.serialization.Serializable

@Serializable
data class SessionDto(
    val sessionId: Int,
    val name: String,
)
