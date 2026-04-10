package com.memowave.app.di

import android.content.Context
import androidx.room.Room
import com.memowave.app.data.local.dao.CategoryDao
import com.memowave.app.data.local.dao.UserDao
import com.memowave.app.data.local.dao.WordDao
import com.memowave.app.data.local.database.MemowaveDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): MemowaveDatabase {
        return Room.databaseBuilder(
            context,
            MemowaveDatabase::class.java,
            "memowave_database"
        )
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }

    @Provides
    @Singleton
    fun provideUserDao(database: MemowaveDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    @Singleton
    fun provideWordDao(database: MemowaveDatabase): WordDao {
        return database.wordDao()
    }

    @Provides
    @Singleton
    fun provideCategoryDao(database: MemowaveDatabase): CategoryDao {
        return database.categoryDao()
    }

    @Provides
    @Singleton
    fun provideSyncQueueDao(database: MemowaveDatabase): com.memowave.app.data.local.dao.SyncQueueDao {
        return database.syncQueueDao()
    }
}