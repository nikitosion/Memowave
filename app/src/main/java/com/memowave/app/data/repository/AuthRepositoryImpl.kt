package com.memowave.app.data.repository

import com.memowave.app.data.remote.api.ApiService
import com.memowave.app.domain.repository.AuthRepository
import kotlinx.coroutines.delay

class AuthRepositoryImpl(
    private val apiService: ApiService
) : AuthRepository {
    override suspend fun login(
        email: String,
        password: String
    ): Result<Unit> {
        delay(1500)
        return Result.success(Unit)
    }
}