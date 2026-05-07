package com.memowave.app.domain.usecase.security

import com.memowave.app.domain.repository.SessionRepository
import javax.inject.Inject

class DenySessionUseCase @Inject constructor(
    private val sessionRepository: SessionRepository,
) {
    suspend operator fun invoke(sessionId: Int): Result<Unit> =
        sessionRepository.denySession(sessionId)
}
