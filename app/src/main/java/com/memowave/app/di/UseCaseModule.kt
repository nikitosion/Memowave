package com.memowave.app.di

import com.memowave.app.core.auth.AuthStateManager
import com.memowave.app.core.diagnostics.DeviceInfoCollector
import com.memowave.app.domain.repository.AuthRepository
import com.memowave.app.domain.repository.SessionRepository
import com.memowave.app.domain.repository.SettingsRepository
import com.memowave.app.domain.usecase.auth.ChangePasswordUseCase
import com.memowave.app.domain.usecase.auth.DeleteAccountUseCase
import com.memowave.app.domain.usecase.auth.LoginUseCase
import com.memowave.app.domain.usecase.auth.LogoutUseCase
import com.memowave.app.domain.usecase.auth.ResetPasswordUseCase
import com.memowave.app.domain.usecase.auth.SignUpUseCase
import com.memowave.app.domain.repository.UserRepository
import com.memowave.app.domain.usecase.profile.UpdateUsernameUseCase
import com.memowave.app.domain.usecase.security.DenySessionUseCase
import com.memowave.app.domain.usecase.security.GetActiveSessionsUseCase
import com.memowave.app.domain.usecase.settings.GetSettingsUseCase
import com.memowave.app.domain.usecase.settings.ResetFsrsToDefaultsUseCase
import com.memowave.app.domain.usecase.settings.UpdateAppLanguageUseCase
import com.memowave.app.domain.usecase.settings.UpdateFsrsConfigUseCase
import com.memowave.app.domain.usecase.settings.UpdateGoalDaysUseCase
import com.memowave.app.domain.usecase.settings.UpdateReduceMotionUseCase
import com.memowave.app.domain.usecase.settings.UpdateReminderTimeUseCase
import com.memowave.app.domain.usecase.settings.UpdateThemeUseCase
import com.memowave.app.domain.usecase.settings.UpdateWeeklyGoalUseCase
import com.memowave.app.domain.validator.PasswordValidator
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
        deviceInfoCollector: DeviceInfoCollector,
    ): SignUpUseCase {
        return SignUpUseCase(authRepository, authStateManager, deviceInfoCollector)
    }

    @Provides
    @Singleton
    fun provideGetActiveSessionsUseCase(
        sessionRepository: SessionRepository,
    ): GetActiveSessionsUseCase = GetActiveSessionsUseCase(sessionRepository)

    @Provides
    @Singleton
    fun provideDenySessionUseCase(
        sessionRepository: SessionRepository,
    ): DenySessionUseCase = DenySessionUseCase(sessionRepository)

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

    @Provides
    @Singleton
    fun provideChangePasswordUseCase(
        authRepository: AuthRepository,
        passwordValidator: PasswordValidator
    ): ChangePasswordUseCase = ChangePasswordUseCase(authRepository, passwordValidator)

    @Provides
    @Singleton
    fun provideDeleteAccountUseCase(
        authRepository: AuthRepository
    ): DeleteAccountUseCase = DeleteAccountUseCase(authRepository)

    @Provides
    @Singleton
    fun provideGetSettingsUseCase(
        repository: SettingsRepository
    ): GetSettingsUseCase = GetSettingsUseCase(repository)

    @Provides
    @Singleton
    fun provideUpdateThemeUseCase(
        repository: SettingsRepository
    ): UpdateThemeUseCase = UpdateThemeUseCase(repository)

    @Provides
    @Singleton
    fun provideUpdateAppLanguageUseCase(
        repository: SettingsRepository
    ): UpdateAppLanguageUseCase = UpdateAppLanguageUseCase(repository)

    @Provides
    @Singleton
    fun provideUpdateReduceMotionUseCase(
        repository: SettingsRepository
    ): UpdateReduceMotionUseCase = UpdateReduceMotionUseCase(repository)

    @Provides
    @Singleton
    fun provideUpdateWeeklyGoalUseCase(
        repository: SettingsRepository
    ): UpdateWeeklyGoalUseCase = UpdateWeeklyGoalUseCase(repository)

    @Provides
    @Singleton
    fun provideUpdateGoalDaysUseCase(
        repository: SettingsRepository
    ): UpdateGoalDaysUseCase = UpdateGoalDaysUseCase(repository)

    @Provides
    @Singleton
    fun provideUpdateReminderTimeUseCase(
        repository: SettingsRepository
    ): UpdateReminderTimeUseCase = UpdateReminderTimeUseCase(repository)

    @Provides
    @Singleton
    fun provideUpdateUsernameUseCase(
        repository: UserRepository
    ): UpdateUsernameUseCase = UpdateUsernameUseCase(repository)

    @Provides
    @Singleton
    fun provideUpdateFsrsConfigUseCase(
        repository: SettingsRepository
    ): UpdateFsrsConfigUseCase = UpdateFsrsConfigUseCase(repository)

    @Provides
    @Singleton
    fun provideResetFsrsToDefaultsUseCase(
        repository: SettingsRepository
    ): ResetFsrsToDefaultsUseCase = ResetFsrsToDefaultsUseCase(repository)
}