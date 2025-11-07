package com.memowave.app.domain.usecase.auth

import com.memowave.app.domain.repository.AuthRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(userId: Long): Result<Unit> {
        val result = authRepository.logout(userId)
        return result
    }
}