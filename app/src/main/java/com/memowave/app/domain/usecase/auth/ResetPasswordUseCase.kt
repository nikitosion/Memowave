package com.memowave.app.domain.usecase.auth

import com.memowave.app.domain.model.User
import com.memowave.app.domain.repository.AuthRepository
import javax.inject.Inject

class ResetPasswordUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        userId: Long,
        newPassword: String
    ): Result<User> {
        val result = authRepository.resetPassword(userId = userId, newPassword = newPassword)
        return result
    }
}