package com.memowave.app.domain.usecase.profile

import com.memowave.app.domain.model.user.User
import com.memowave.app.domain.repository.UserRepository
import javax.inject.Inject

class UpdateUsernameUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(newUsername: String): Result<User> {
        val trimmed = newUsername.trim()
        if (trimmed.length < MIN_LENGTH) {
            return Result.failure(IllegalArgumentException(ERR_TOO_SHORT))
        }
        if (trimmed.length > MAX_LENGTH) {
            return Result.failure(IllegalArgumentException(ERR_TOO_LONG))
        }
        return repository.updateUsername(trimmed)
    }

    companion object {
        const val MIN_LENGTH = 2
        const val MAX_LENGTH = 32
        const val ERR_TOO_SHORT = "username_too_short"
        const val ERR_TOO_LONG = "username_too_long"
    }
}
