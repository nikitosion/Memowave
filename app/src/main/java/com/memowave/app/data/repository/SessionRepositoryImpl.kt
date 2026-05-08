package com.memowave.app.data.repository

import com.memowave.app.data.mapper.SessionMapper
import com.memowave.app.data.remote.api.ApiService
import com.memowave.app.domain.model.security.UserSession
import com.memowave.app.domain.repository.SessionRepository
import javax.inject.Inject

class SessionRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val sessionMapper: SessionMapper,
) : SessionRepository {

    override suspend fun getActiveSessions(): Result<List<UserSession>> {
        return try {
            val response = apiService.getActiveSessions()
            if (!response.isSuccessful) {
                return Result.failure(Exception("Не удалось загрузить сессии: ${response.code()}"))
            }
            val sessions = response.body().orEmpty().map(sessionMapper::dtoToDomain)
            Result.success(sessions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun denySession(sessionId: Int): Result<Unit> {
        return try {
            val response = apiService.denySession(sessionId)
            if (!response.isSuccessful) {
                return Result.failure(Exception("Не удалось завершить сессию: ${response.code()}"))
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
