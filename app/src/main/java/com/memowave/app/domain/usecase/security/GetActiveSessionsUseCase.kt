package com.memowave.app.domain.usecase.security

import com.memowave.app.domain.model.security.UserSession
import com.memowave.app.domain.repository.SessionRepository
import javax.inject.Inject

class GetActiveSessionsUseCase @Inject constructor(
    private val sessionRepository: SessionRepository,
) {
    suspend operator fun invoke(): Result<List<UserSession>> =
        sessionRepository.getActiveSessions()
}
