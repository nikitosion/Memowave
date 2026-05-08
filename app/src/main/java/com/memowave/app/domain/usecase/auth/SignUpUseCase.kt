package com.memowave.app.domain.usecase.auth

import com.memowave.app.core.auth.AuthStateManager
import com.memowave.app.core.diagnostics.DeviceInfoCollector
import com.memowave.app.domain.model.user.UserRegistration
import com.memowave.app.domain.repository.AuthRepository
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val authStateManager: AuthStateManager,
    private val deviceInfoCollector: DeviceInfoCollector,
) {
    suspend operator fun invoke(
        username: String,
        email: String,
        password: String,
    ): Result<Unit> {

        // TODO: Remove default image URL when profile picture upload is implemented
        val newUser = UserRegistration(
            username = username,
            email = email,
            password = password,
            imageUrl = "https://example.com/default-profile.png",
            session = deviceInfoCollector.sessionName(),
        )

        val result = authRepository.register(newUser = newUser)
        if (result.isSuccess) {
            authStateManager.setAuthenticated()
        }
        return result
    }
}
