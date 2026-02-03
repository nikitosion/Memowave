package com.memowave.app.domain.usecase.auth

import com.memowave.app.domain.model.user.User
import com.memowave.app.domain.repository.AuthRepository
import javax.inject.Inject

class GetUserByEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String): Result<User?> {
        val result = authRepository.getUserByEmail(email = email)
        return result
    }
}