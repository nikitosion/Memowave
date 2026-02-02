package com.memowave.app.domain.usecase.auth

import com.memowave.app.core.auth.AuthStateManager
import com.memowave.app.domain.repository.AuthRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val authStateManager: AuthStateManager
) {
    suspend operator fun invoke(): Result<Unit> {
        authStateManager.setUnauthenticated()
        val result = authRepository.logout()
        return result
    }
}