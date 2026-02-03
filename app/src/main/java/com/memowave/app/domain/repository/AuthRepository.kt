package com.memowave.app.domain.repository

import com.memowave.app.domain.model.User
import com.memowave.app.domain.model.UserRegistration

interface AuthRepository {
    suspend fun getUserByEmail(email: String): Result<User?>
    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun register(newUser: UserRegistration): Result<User>
    suspend fun resetPassword(userId: Long, newPassword: String): Result<User>
    suspend fun logout(): Result<Unit>
    suspend fun checkTokenExist(): Result<Boolean>
}