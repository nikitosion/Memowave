package com.memowave.app.di

import com.memowave.app.data.local.TokenManager
import com.memowave.app.data.local.dao.CategoryDao
import com.memowave.app.data.local.dao.SyncQueueDao
import com.memowave.app.data.local.dao.UserDao
import com.memowave.app.data.local.dao.WordDao
import com.memowave.app.data.local.database.MemowaveDatabase
import com.memowave.app.data.mapper.CategoryMapper
import com.memowave.app.data.mapper.UserMapper
import com.memowave.app.data.mapper.WordMapper
import com.memowave.app.data.remote.api.ApiService
import com.memowave.app.data.repository.AuthRepositoryImpl
import com.memowave.app.data.repository.CategoryRepositoryImpl
import com.memowave.app.data.repository.SettingsRepositoryImpl
import com.memowave.app.data.repository.UserRepositoryImpl
import com.memowave.app.data.repository.WordRepositoryImpl
import com.memowave.app.data.sync.SyncManager
import com.memowave.app.domain.repository.AuthRepository
import com.memowave.app.domain.repository.CategoryRepository
import com.memowave.app.domain.repository.SettingsRepository
import com.memowave.app.domain.repository.UserRepository
import com.memowave.app.domain.repository.WordRepository
import com.memowave.app.domain.model.settings.AppSettings
import androidx.datastore.core.DataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        apiService: ApiService,
        userDao: UserDao,
        userMapper: UserMapper,
        tokenManager: TokenManager
    ): AuthRepository {
        return AuthRepositoryImpl(
            apiService = apiService,
            userDao = userDao,
            userMapper = userMapper,
            tokenManager = tokenManager
        )
    }

    @Provides
    @Singleton
    fun provideUserMapper(): UserMapper {
        return UserMapper()
    }

    @Provides
    @Singleton
    fun provideUserRepository(
        apiService: ApiService,
        userMapper: UserMapper
    ): UserRepository {
        return UserRepositoryImpl(
            apiService = apiService,
            userMapper = userMapper
        )
    }

    @Provides
    @Singleton
    fun provideWordMapper(): WordMapper {
        return WordMapper()
    }

    @Provides
    @Singleton
    fun provideWordRepository(
        wordDao: WordDao,
        syncQueueDao: SyncQueueDao,
        apiService: ApiService,
        wordMapper: WordMapper,
        database: MemowaveDatabase,
        syncManager: SyncManager
    ): WordRepository {
        return WordRepositoryImpl(
            wordDao = wordDao,
            syncQueueDao = syncQueueDao,
            apiService = apiService,
            wordMapper = wordMapper,
            database = database,
            syncManager = syncManager
        )
    }

    @Provides
    @Singleton
    fun provideCategoryMapper(): CategoryMapper {
        return CategoryMapper()
    }

    @Provides
    @Singleton
    fun provideCategoryRepository(
        categoryDao: CategoryDao,
        syncQueueDao: SyncQueueDao,
        apiService: ApiService,
        categoryMapper: CategoryMapper,
        database: MemowaveDatabase,
        syncManager: SyncManager
    ): CategoryRepository {
        return CategoryRepositoryImpl(
            categoryDao = categoryDao,
            syncQueueDao = syncQueueDao,
            apiService = apiService,
            categoryMapper = categoryMapper,
            database = database,
            syncManager = syncManager
        )
    }

    @Provides
    @Singleton
    fun provideSettingsRepository(
        dataStore: DataStore<AppSettings>
    ): SettingsRepository = SettingsRepositoryImpl(dataStore)
}