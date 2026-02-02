package com.memowave.app.domain.usecase.auth

import com.memowave.app.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String
    ): Result<Unit> {
        val result = authRepository.login(email = email, password = password)
        return result
    }
}