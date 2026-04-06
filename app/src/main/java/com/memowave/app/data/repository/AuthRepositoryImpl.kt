package com.memowave.app.data.repository

import com.memowave.app.data.local.TokenManager
import com.memowave.app.data.local.dao.UserDao
import com.memowave.app.data.mapper.UserMapper
import com.memowave.app.data.remote.api.ApiService
import com.memowave.app.data.remote.dto.user.UserLoginReqDto
import com.memowave.app.domain.model.user.User
import com.memowave.app.domain.model.user.UserRegistration
import com.memowave.app.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val userDao: UserDao,
    private val userMapper: UserMapper,
    private val tokenManager: TokenManager,
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
            val userRequest = UserLoginReqDto(email = email, password = password)
            val response = apiService.login(userRequest)

            if (!response.isSuccessful) {
                return Result.failure(Exception("Ошибка при входе: ${response.code()}"))
            }

            val token =
                response.body()?.token ?: return Result.failure(Exception("Токен не получен"))

            tokenManager.saveToken(token)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun checkTokenExist(): Result<Boolean> {
        return try {
            val token = tokenManager.getTokenSync()
            if (token.isNullOrEmpty()) {
                return Result.success(false)
            }
            Result.success(true)
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

    override suspend fun logout(): Result<Unit> {
        return try {
            tokenManager.clear()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}