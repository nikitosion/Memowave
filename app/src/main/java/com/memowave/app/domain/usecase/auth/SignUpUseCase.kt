package com.memowave.app.domain.usecase.auth

import com.memowave.app.domain.model.user.UserRegistration
import com.memowave.app.domain.repository.AuthRepository
import com.memowave.app.ui.common.notification.NotificationManager
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val notificationManager: NotificationManager
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
            imageUrl = "https://example.com/default-profile.png"
        )

        val result = authRepository.register(newUser = newUser)

        if (result.isSuccess) {
            notificationManager.showSuccess("Successfully signed up! Please log in.")
        } else {
            notificationManager.showError("Sign up failed. Please try again.")
        }

        return result
    }
}