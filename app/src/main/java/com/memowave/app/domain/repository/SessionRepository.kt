package com.memowave.app.domain.repository

import com.memowave.app.domain.model.security.UserSession

interface SessionRepository {
    suspend fun getActiveSessions(): Result<List<UserSession>>
    suspend fun denySession(sessionId: Int): Result<Unit>
}
