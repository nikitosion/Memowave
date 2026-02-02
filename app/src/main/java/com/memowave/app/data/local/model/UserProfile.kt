package com.memowave.app.data.local.model

import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val id: String = "",
    val username: String = "",
    val email: String = "",
    val imageUrl: String = "",
)