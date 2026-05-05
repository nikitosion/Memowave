package com.memowave.app.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokenReqDto(
    val refreshToken: String,
)
