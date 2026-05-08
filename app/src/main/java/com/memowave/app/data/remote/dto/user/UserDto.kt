package com.memowave.app.data.remote.dto.user

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: Long? = null,
    val username: String?,
    val imageUrl: String?,
    val email: String?,
    val experience: Int? = null,
)