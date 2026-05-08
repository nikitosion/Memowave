package com.memowave.app.core.notifications

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.memowave.app.domain.model.settings.AppSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Планировщик ежедневных напоминаний.
 *
 * Использует WorkManager periodic с периодом 24 часа и initial delay до ближайшего
 * слота `(reminderTime, day in goalDaysOfWeek)`. Сам [ReminderWorker] на каждом тике
 * перепроверяет настройки и решает, постить ли уведомление прямо сейчас.
 */
@Singleton
class ReminderScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun schedule(settings: AppSettings, now: LocalDateTime = LocalDateTime.now()) {
        if (!settings.notificationsEnabled || !settings.studyRemindersEnabled) {
            cancel()
            return
        }
        if (settings.goalDaysOfWeek.isEmpty()) {
            cancel()
            return
        }

        val initialDelay = computeInitialDelayMillis(settings, now)
        val request = PeriodicWorkRequestBuilder<ReminderWorker>(
            REPEAT_INTERVAL_HOURS, TimeUnit.HOURS
        )
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            UNIQUE_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    fun cancel() {
        WorkManager.getInstance(context).cancelUniqueWork(UNIQUE_WORK_NAME)
    }

    /**
     * Вычисляет миллисекунды до ближайшего слота "учебный день + время напоминания".
     * Перебирает дни вперёд от текущего; если сегодня нужный день и время ещё не прошло —
     * возвращает остаток до сегодняшнего срабатывания, иначе ищет следующий день.
     */
    private fun computeInitialDelayMillis(
        settings: AppSettings,
        now: LocalDateTime
    ): Long {
        val target = LocalTime.of(settings.reminderHour, settings.reminderMinute)
        for (offset in 0..7) {
            val candidate = now.toLocalDate().plusDays(offset.toLong())
            val dayIso = candidate.dayOfWeek.value
            if (dayIso !in settings.goalDaysOfWeek) continue
            val candidateAt = LocalDateTime.of(candidate, target)
            if (candidateAt.isAfter(now)) {
                return Duration.between(now, candidateAt).toMillis().coerceAtLeast(0)
            }
        }
        // Fallback — should never reach here unless goalDaysOfWeek is empty
        val fallback = LocalDateTime.of(LocalDate.now().plusDays(1), target)
        return Duration.between(now, fallback).toMillis().coerceAtLeast(0)
    }

    private companion object {
        const val UNIQUE_WORK_NAME = "memowave_study_reminder_v1"
        const val REPEAT_INTERVAL_HOURS = 24L
    }

    @Suppress("unused") // exposed for tests
    fun isScheduledDay(day: DayOfWeek, settings: AppSettings): Boolean =
        day.value in settings.goalDaysOfWeek
}
