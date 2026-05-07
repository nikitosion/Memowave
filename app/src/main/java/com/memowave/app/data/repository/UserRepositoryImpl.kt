package com.memowave.app.data.repository

import com.memowave.app.data.mapper.UserMapper
import com.memowave.app.data.remote.api.ApiService
import com.memowave.app.domain.model.user.User
import com.memowave.app.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val userMapper: UserMapper
) : UserRepository {
    override suspend fun getUserInfo(): Result<User> {
        return try {
            val response = apiService.getUserInfo()
            if (!response.isSuccessful) {
                return Result.failure(Exception("Ошибка при получении профиля: ${response.code()}"))
            }
            val userDto = response.body() ?: return Result.failure(Exception("Профиль не найден"))

            Result.success(userMapper.dtoToDomain(userDto))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUsername(newUsername: String): Result<User> {
        return try {
            val current = apiService.getUserInfo()
            if (!current.isSuccessful) {
                return Result.failure(Exception("Не удалось получить текущий профиль: ${current.code()}"))
            }
            val currentDto = current.body()
                ?: return Result.failure(Exception("Профиль не найден"))

            val payload = currentDto.copy(username = newUsername)
            val response = apiService.updateUserInfo(payload)
            if (!response.isSuccessful) {
                return Result.failure(Exception("Не удалось обновить имя: ${response.code()}"))
            }
            val updated = response.body() ?: payload
            Result.success(userMapper.dtoToDomain(updated))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
