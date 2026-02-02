package com.memowave.app.data.repository

import com.memowave.app.data.local.TokenManager
import com.memowave.app.data.local.dao.UserDao
import com.memowave.app.data.mapper.UserMapper
import com.memowave.app.data.remote.api.ApiService
import com.memowave.app.data.remote.dto.user.UserLoginDto
import com.memowave.app.domain.model.User
import com.memowave.app.domain.model.UserRegistration
import com.memowave.app.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val userDao: UserDao,
    private val userMapper: UserMapper,
    private val tokenManager: TokenManager
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
            val userRequest = UserLoginDto(username = email, password = password)
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

    override suspend fun register(
        newUser: UserRegistration
    ): Result<User> {
        return try {
            val existingUser = userDao.getUserByEmail(newUser.email)
            if (existingUser != null) {
                return Result.failure(Exception("Пользователь с таким email уже существует"))
            }

            val userId = userDao.createUser(userMapper.userRegistrationToEntity(newUser))
            val user = User(id = userId, username = newUser.username, email = newUser.email)

            Result.success(user)
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

    override suspend fun logout(userId: Long): Result<Unit> {
        return try {
            userDao.getUserById(userId)
                ?: return Result.failure(Exception("Пользователь не найден"))

            userDao.deleteUserById(userId)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}