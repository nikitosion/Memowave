package com.memowave.app.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.memowave.app.data.local.dao.CategoryDao
import com.memowave.app.data.local.dao.SyncQueueDao
import com.memowave.app.data.local.dao.UserDao
import com.memowave.app.data.local.dao.WordDao
import com.memowave.app.data.local.entity.CategoryEntity
import com.memowave.app.data.local.entity.SyncQueueEntity
import com.memowave.app.data.local.entity.UserEntity
import com.memowave.app.data.local.entity.WordEntity

@Database(
    entities = [
        UserEntity::class,
        WordEntity::class,
        CategoryEntity::class,
        SyncQueueEntity::class
    ],
    version = 1
)
@TypeConverters(Converters::class)
abstract class MemowaveDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun wordDao(): WordDao
    abstract fun categoryDao(): CategoryDao
    abstract fun syncQueueDao(): SyncQueueDao
}