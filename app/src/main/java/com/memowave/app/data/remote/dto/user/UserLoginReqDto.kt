package com.memowave.app.data.remote.dto.user

import kotlinx.serialization.Serializable

@Serializable
data class UserLoginReqDto(
    val email: String,
    val password: String,
    val session: String,
)

