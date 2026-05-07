package com.memowave.app.domain.usecase.auth

import com.memowave.app.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Stub for delete account flow. Backend endpoint is not implemented yet —
 * call returns Result.failure(NotImplementedError) so UI can show "Coming soon".
 */
class DeleteAccountUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<Unit> = authRepository.deleteAccount()
}
