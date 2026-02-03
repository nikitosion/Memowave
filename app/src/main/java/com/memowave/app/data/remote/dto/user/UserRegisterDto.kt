package com.memowave.app.data.remote.dto.user

import kotlinx.serialization.Serializable

@Serializable
data class UserRegisterDto(
    val username: String,
    val email: String,
    val password: String,
)