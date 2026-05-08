package com.memowave.app.data.remote.dto.user

import kotlinx.serialization.Serializable

@Serializable
data class ChangePasswordDto (
    val currentPassword: String,
    val newPassword: String
)