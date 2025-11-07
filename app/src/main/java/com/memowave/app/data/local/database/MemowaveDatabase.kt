package com.memowave.app.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.memowave.app.data.local.dao.UserDao
import com.memowave.app.data.local.entity.UserEntity

@Database(entities = [UserEntity::class], version = 1)
abstract class MemowaveDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}