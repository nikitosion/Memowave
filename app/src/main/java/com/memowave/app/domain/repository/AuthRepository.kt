package com.memowave.app.domain.repository

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<Unit>
}