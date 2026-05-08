package com.memowave.app

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.BackoffPolicy
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import com.memowave.app.core.notifications.MemowaveNotificationChannels
import com.memowave.app.core.notifications.ReminderScheduler
import com.memowave.app.data.local.TokenManager
import com.memowave.app.data.sync.SyncWorker
import com.memowave.app.di.ApplicationScope
import com.memowave.app.domain.repository.SettingsRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class MemowaveApp : Application(), Configuration.Provider, SingletonImageLoader.Factory {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var tokenManager: TokenManager

    @Inject
    lateinit var imageLoader: ImageLoader

    @Inject
    lateinit var reminderScheduler: ReminderScheduler

    @Inject
    lateinit var settingsRepository: SettingsRepository

    @Inject
    @ApplicationScope
    lateinit var applicationScope: CoroutineScope

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun newImageLoader(context: PlatformContext): ImageLoader = imageLoader

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        // Prime the JWT cache from disk *before* any network request can fire,
        // so AuthInterceptor sees a non-null token on the very first call (e.g. the
        // auto-login probe in LoginViewModel.init). One-time DataStore read.
        tokenManager.primeBlocking()
        scheduleSyncWorker()
        MemowaveNotificationChannels.ensureCreated(this)
        observeReminderSettings()
        rolloverStaleStreakOnce()
    }

    private fun scheduleSyncWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                1, TimeUnit.MINUTES
            )
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "sync_pending_operations",
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )
    }

    private fun observeReminderSettings() {
        // TODO: модуль уведомлений временно отключён. Принудительно снимаем любые
        // запланированные WorkManager-задачи и не подписываемся на изменения настроек.
        // Когда фича вернётся — восстановить подписку на settings flow и schedule().
        reminderScheduler.cancel()
    }

    /**
     * Если последний день стрика старше «вчера», обнуляем его текущее значение.
     * Лучший рекорд (`longestStreak`) сохраняем. Запускается один раз на старте.
     */
    private fun rolloverStaleStreakOnce() {
        applicationScope.launch {
            val s = settingsRepository.getSettings().first()
            val today = LocalDate.now()
            val yesterdayIso = today.minusDays(1).toString()
            val todayIso = today.toString()
            val streakAlive = s.lastStreakDate == todayIso || s.lastStreakDate == yesterdayIso
            val activityToday = s.lastActivityDate == todayIso
            if (!streakAlive && s.currentStreak != 0) {
                settingsRepository.setStreakState(
                    currentStreak = 0,
                    longestStreak = s.longestStreak,
                    lastStreakDate = s.lastStreakDate,
                    wordsCompletedToday = if (activityToday) s.wordsCompletedToday else 0,
                    lastActivityDate = if (activityToday) s.lastActivityDate else null,
                )
            } else if (!activityToday && s.wordsCompletedToday != 0) {
                // Стрик ещё жив (вчерашняя дата), но дневной счётчик протух — сбрасываем,
                // чтобы UI сразу показывал 0/N с момента запуска.
                settingsRepository.setStreakState(
                    currentStreak = s.currentStreak,
                    longestStreak = s.longestStreak,
                    lastStreakDate = s.lastStreakDate,
                    wordsCompletedToday = 0,
                    lastActivityDate = null,
                )
            }
        }
    }
}
