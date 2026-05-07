package com.memowave.app.domain.repository

import com.memowave.app.domain.model.user.User
import com.memowave.app.domain.model.user.UserRegistration

interface AuthRepository {
    suspend fun getUserByEmail(email: String): Result<User?>
    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun register(newUser: UserRegistration): Result<Unit>
    suspend fun resetPassword(userId: Long, newPassword: String): Result<User>
    suspend fun changePassword(currentPassword: String, newPassword: String): Result<Unit>
    suspend fun deleteAccount(): Result<Unit>
    suspend fun logout(): Result<Unit>
}
