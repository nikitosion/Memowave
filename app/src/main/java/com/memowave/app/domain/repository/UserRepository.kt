package com.memowave.app.domain.repository

import com.memowave.app.domain.model.User

interface UserRepository {
    suspend fun getUserProfileInfo(userId: Long): Result<User>
}