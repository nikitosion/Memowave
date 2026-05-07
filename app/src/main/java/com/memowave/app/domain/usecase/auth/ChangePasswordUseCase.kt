package com.memowave.app.domain.usecase.auth

import com.memowave.app.domain.repository.AuthRepository
import com.memowave.app.domain.validator.PasswordValidator
import javax.inject.Inject

class ChangePasswordUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val passwordValidator: PasswordValidator
) {
    suspend operator fun invoke(
        currentPassword: String,
        newPassword: String
    ): Result<Unit> {
        if (currentPassword == newPassword) {
            return Result.failure(ChangePasswordError.SamePassword)
        }
        if (!passwordValidator.validate(newPassword).isValid) {
            return Result.failure(ChangePasswordError.InvalidPassword)
        }
        return authRepository.changePassword(currentPassword, newPassword)
    }
}
