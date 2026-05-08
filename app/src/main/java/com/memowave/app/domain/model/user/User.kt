package com.memowave.app.domain.model.user

data class User(
    val id: Long? = 0,
    val username: String? = "",
    val email: String? = "",
    val imageUrl: String? = null,
    val experience: Int = 0,
)
