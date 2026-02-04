package com.memowave.app.data.remote.dto.user

import kotlinx.serialization.Serializable

@Serializable
data class UserLoginReqDto(
    val username: String,
    val password: String,
)

