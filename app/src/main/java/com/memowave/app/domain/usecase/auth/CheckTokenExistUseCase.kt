package com.memowave.app.domain.usecase.auth

import com.memowave.app.core.auth.AuthStateManager
import com.memowave.app.domain.repository.AuthRepository
import javax.inject.Inject

class CheckTokenExistUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val authStateManager: AuthStateManager
) {
    suspend operator fun invoke(): Result<Boolean> {
        val result = authRepository.checkTokenExist()
        if (result.isSuccess) {
            val tokenExists = result.getOrDefault(false)
            if (tokenExists) {
                authStateManager.setAuthenticated()
            }
        }
        return result
    }
}
