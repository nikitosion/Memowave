package com.memowave.app.data.repository

import com.memowave.app.core.diagnostics.DeviceInfoCollector
import com.memowave.app.data.local.TokenManager
import com.memowave.app.data.local.dao.UserDao
import com.memowave.app.data.mapper.UserMapper
import com.memowave.app.data.remote.api.ApiService
import com.memowave.app.data.remote.dto.user.ChangePasswordDto
import com.memowave.app.data.remote.dto.user.UserLoginReqDto
import com.memowave.app.domain.model.user.User
import com.memowave.app.domain.model.user.UserRegistration
import com.memowave.app.domain.repository.AuthRepository
import com.memowave.app.domain.usecase.auth.ChangePasswordError
import com.memowave.app.domain.usecase.auth.VerifyEmailError
import javax.inject.Inject
import kotlinx.coroutines.withTimeoutOrNull

class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val userDao: UserDao,
    private val userMapper: UserMapper,
    private val tokenManager: TokenManager,
    private val deviceInfoCollector: DeviceInfoCollector,
) : AuthRepository {
    override suspend fun getUserByEmail(email: String): Result<User?> {
        return try {
            val userEntity = userDao.getUserByEmail(email = email)
            if (userEntity == null) {
                return Result.failure(Exception("Нет пользователя с таким email"))
            }

            val user = userMapper.entityToDomainUser(userEntity)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            val sessionName = deviceInfoCollector.sessionName()
            val userRequest = UserLoginReqDto(
                email = email,
                password = password,
                session = sessionName,
            )
            val response = apiService.login(userRequest)

            if (!response.isSuccessful) {
                return Result.failure(Exception("Ошибка при входе: ${response.code()}"))
            }

            val body = response.body()
                ?: return Result.failure(Exception("Токены не получены"))

            tokenManager.saveTokens(body.accessToken, body.refreshToken)
            resolveAndSaveCurrentSessionId(sessionName)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(newUser: UserRegistration): Result<Unit> {
        return try {
            val response = apiService.register(newUser)
            if (!response.isSuccessful) {
                return Result.failure(Exception("Ошибка при регистрации: ${response.code()}"))
            }

            val body = response.body()
                ?: return Result.failure(Exception("Токены не получены"))

            tokenManager.saveTokens(body.accessToken, body.refreshToken)
            resolveAndSaveCurrentSessionId(newUser.session)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun resetPassword(userId: Long, newPassword: String): Result<User> {
        return try {
            val user = userDao.getUserById(userId)
            if (user == null) {
                return Result.failure(Exception("Пользователь не найден"))
            }

            userDao.updatePassword(id = user.id, password = newPassword)
            val updatedUser = userMapper.entityToDomainUser(user.copy(password = newPassword))

            Result.success(updatedUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun changePassword(
        currentPassword: String,
        newPassword: String
    ): Result<Unit> {
        return try {
            val response = apiService.changePassword(
                ChangePasswordDto(currentPassword = currentPassword, newPassword = newPassword)
            )
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(
                    when (response.code()) {
                        401 -> ChangePasswordError.WrongCurrentPassword
                        403 -> ChangePasswordError.Forbidden
                        in 500..599 -> ChangePasswordError.ServerError
                        else -> ChangePasswordError.Unknown
                    }
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun sendCode(userId: Long): Result<Unit> {
        return try {
            val response = apiService.sendCode(userId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(
                    when (response.code()) {
                        403 -> VerifyEmailError.Forbidden
                        else -> VerifyEmailError.SendFailed
                    }
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun verifyEmail(userId: Long, code: String): Result<Unit> {
        return try {
            val response = apiService.verifyEmail(userId, code)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(
                    when (response.code()) {
                        401 -> VerifyEmailError.InvalidCode
                        403 -> VerifyEmailError.Forbidden
                        else -> VerifyEmailError.Unknown
                    }
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteAccount(): Result<Unit> {
        return Result.failure(NotImplementedError("Delete account is not implemented yet"))
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            // Best-effort: deny the current session on the server before clearing
            // local tokens. Time-bound so a slow/offline backend doesn't hang
            // the logout UI; failures are swallowed — the user logs out either way.
            val sessionId = tokenManager.getSessionId()
            if (sessionId != null) {
                withTimeoutOrNull(LOGOUT_DENY_TIMEOUT_MS) {
                    runCatching { apiService.denySession(sessionId) }
                }
            }
            tokenManager.clear()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Auth response doesn't include sessionId, so after a successful login/register
     * we read GET /sessions and pick the last entry whose name matches what we sent
     * (assuming the server orders by ascending creation time, the freshest one is ours).
     * Failures are intentionally swallowed: the auth flow already succeeded, we just
     * won't be able to mark "Current" in the security screen until next refresh.
     */
    private suspend fun resolveAndSaveCurrentSessionId(name: String) {
        runCatching {
            val sessions = apiService.getActiveSessions().body().orEmpty()
            sessions.lastOrNull { it.name == name }?.sessionId
                ?.let { tokenManager.saveSessionId(it) }
        }
    }

    private companion object {
        const val LOGOUT_DENY_TIMEOUT_MS = 3_000L
    }
}
