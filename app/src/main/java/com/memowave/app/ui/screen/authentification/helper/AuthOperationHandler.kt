package com.memowave.app.ui.screen.authentification.helper

import com.memowave.app.domain.usecase.auth.CheckTokenExistUseCase
import com.memowave.app.domain.usecase.auth.GetUserByEmailUseCase
import com.memowave.app.domain.usecase.auth.LoginUseCase
import com.memowave.app.domain.usecase.auth.ResetPasswordUseCase
import com.memowave.app.domain.usecase.auth.SignUpUseCase
import kotlinx.coroutines.delay
import javax.inject.Inject

/**
 * AuthOperationHandler is responsible for executing authentication-related operations
 * such as login, sign up, password reset, and user lookup by email.
 * It delegates business logic to domain use cases and returns unified result types
 * for easy handling in the ViewModel and UI layer.
 * - Performs login, sign up, password reset;
 * - Handles exceptions and maps them to result types;
 * - Returns AuthResult or GetUserResult for each operation;
 *
 * @constructor Injects domain use cases for authentication operations
 * @property loginUseCase Use case for user login
 * @property signUpUseCase Use case for user registration
 * @property getUserByEmailUseCase Use case for user lookup by email
 * @property resetPasswordUseCase Use case for password reset
 */
class AuthOperationHandler @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val signUpUseCase: SignUpUseCase,
    private val getUserByEmailUseCase: GetUserByEmailUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase,
    private val checkTokenExistUseCase: CheckTokenExistUseCase
) {
    suspend fun checkTokenExist(): Boolean {
        val result = checkTokenExistUseCase()
        return result.getOrElse { false }
    }

    /**
     * Performs user login operation.
     * @param email User email
     * @param password User password
     * @return AuthResult indicating success, failure, or error
     */
    suspend fun performLogin(email: String, password: String): AuthResult {
        return try {
            val result = loginUseCase(email, password)

            if (result.isSuccess) {
                AuthResult.Success
            } else {
                AuthResult.Failure("Неверный email или пароль")
            }
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Ошибка при входе")
        }
    }

    /**
     * Performs user registration operation.
     * @param username User name
     * @param email User email
     * @param password User password

     * @return AuthResult indicating success, failure, or error
     */
    suspend fun performSignUp(
        username: String,
        email: String,
        password: String
    ): AuthResult {
        return try {
            val result = signUpUseCase(username = username, email = email, password = password)

            if (result.isSuccess) {
                AuthResult.Success
            } else {
                AuthResult.Failure("Что-то пошло не так при регистрации")
            }
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Ошибка при регистрации")
        }
    }

    /**
     * Looks up a user by email.
     * @param email User email
     * @return GetUserResult indicating success (with userId), failure, or error
     */
    suspend fun performGetUserByEmail(email: String): GetUserResult {
        return try {
            delay(1500) // TODO: Remove in production
            val result = getUserByEmailUseCase(email)

            if (result.isSuccess) {
                val userId = result.getOrNull()?.id
                if (userId != null) {
                    GetUserResult.Success(userId)
                } else {
                    GetUserResult.Failure("Пользователь не найден")
                }
            } else {
                GetUserResult.Failure("Некорректный email")
            }
        } catch (e: Exception) {
            GetUserResult.Error(e.message ?: "Ошибка при поиске пользователя")
        }
    }

    /**
     * Performs password reset operation for a user.
     * @param userId User ID
     * @param newPassword New password
     * @return AuthResult indicating success, failure, or error
     */
    suspend fun performResetPassword(userId: Long, newPassword: String): AuthResult {
        return try {
            delay(1500) // TODO: Remove in production
            val result = resetPasswordUseCase(userId, newPassword)

            if (result.isSuccess) {
                AuthResult.Success
            } else {
                AuthResult.Failure("Что-то пошло не так при сбросе пароля")
            }
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Ошибка при сбросе пароля")
        }
    }
}

sealed class AuthResult {
    data object Success : AuthResult()
    data class Failure(val message: String) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

sealed class GetUserResult {
    data class Success(val userId: Long) : GetUserResult()
    data class Failure(val message: String) : GetUserResult()
    data class Error(val message: String) : GetUserResult()
}
