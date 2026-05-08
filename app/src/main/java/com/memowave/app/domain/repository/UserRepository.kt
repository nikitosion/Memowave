package com.memowave.app.domain.repository

import com.memowave.app.domain.model.user.User

interface UserRepository {
    suspend fun getUserInfo(): Result<User>
    suspend fun updateUsername(newUsername: String): Result<User>
    suspend fun addExperience(xpDelta: Int)
}