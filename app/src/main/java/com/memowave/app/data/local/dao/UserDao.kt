package com.memowave.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.ABORT
import androidx.room.Query
import com.memowave.app.data.local.entity.UserEntity

@Dao
interface UserDao {
    @Insert(onConflict = ABORT)
    suspend fun createUser(user: UserEntity): Long

    @Query("SELECT * FROM users LIMIT 1")
    suspend fun getCurrentUser(): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Long): UserEntity?

    @Query("UPDATE users SET password = :password WHERE id = :id")
    suspend fun updatePassword(id: Long, password: String)

    @Query("DELETE FROM users WHERE id = :id")
    suspend fun deleteUserById(id: Long)
}