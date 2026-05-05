package com.memowave.app.di

import com.memowave.app.core.auth.AuthStateManager
import com.memowave.app.domain.repository.AuthRepository
import com.memowave.app.domain.usecase.auth.LoginUseCase
import com.memowave.app.domain.usecase.auth.LogoutUseCase
import com.memowave.app.domain.usecase.auth.ResetPasswordUseCase
import com.memowave.app.domain.usecase.auth.SignUpUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideLoginUseCase(
        authRepository: AuthRepository,
        authStateManager: AuthStateManager
    ): LoginUseCase {
        return LoginUseCase(authRepository, authStateManager)
    }

    @Provides
    @Singleton
    fun provideSignUpUseCase(
        authRepository: AuthRepository,
        authStateManager: AuthStateManager,
    ): SignUpUseCase {
        return SignUpUseCase(authRepository, authStateManager)
    }

    @Provides
    @Singleton
    fun provideResetPasswordUseCase(authRepository: AuthRepository): ResetPasswordUseCase {
        return ResetPasswordUseCase(authRepository)
    }

    @Provides
    @Singleton
    fun provideLogoutUseCase(
        authRepository: AuthRepository,
        authStateManager: AuthStateManager
    ): LogoutUseCase {
        return LogoutUseCase(
            authRepository,
            authStateManager
        )
    }
}