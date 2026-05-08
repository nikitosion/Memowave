package com.memowave.app.domain.usecase.auth

import com.memowave.app.domain.repository.AuthRepository
import javax.inject.Inject

class VerifyEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(userId: Long, code: String): Result<Unit> =
        authRepository.verifyEmail(userId, code)
}
