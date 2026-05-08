package com.memowave.app.domain.usecase.auth

import com.memowave.app.domain.repository.AuthRepository
import javax.inject.Inject

class SendCodeUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(userId: Long): Result<Unit> =
        authRepository.sendCode(userId)
}
