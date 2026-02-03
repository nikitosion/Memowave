package com.memowave.app.domain.model.user

import kotlinx.serialization.Serializable

@Serializable
data class UserRegistration(
    val username: String,
    val password: String,
    val imageUrl: String,
    val email: String,
)