package com.memowave.app.domain.usecase.auth

import com.memowave.app.domain.model.User
import com.memowave.app.domain.model.UserRegistration
import com.memowave.app.domain.repository.AuthRepository
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        username: String,
        email: String,
        password: String
    ): Result<User> {

        val newUser = UserRegistration(
            username = username,
            email = email,
            password = password
        )

        val result = authRepository.register(newUser = newUser)
        return result
    }
}