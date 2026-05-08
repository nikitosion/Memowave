package com.memowave.app.domain.usecase.auth

import com.memowave.app.core.auth.AuthStateManager
import com.memowave.app.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val authStateManager: AuthStateManager
) {
    suspend operator fun invoke(
        email: String,
        password: String
    ): Result<Unit> {
        val result = authRepository.login(email = email, password = password)
        if (result.isSuccess) {
            authStateManager.setAuthenticated()
        }
        return result
    }
}