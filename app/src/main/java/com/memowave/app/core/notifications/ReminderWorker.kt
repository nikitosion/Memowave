package com.memowave.app.core.notifications

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.memowave.app.R
import com.memowave.app.domain.model.settings.AppSettings
import com.memowave.app.domain.usecase.settings.GetSettingsUseCase
import com.memowave.app.ui.MainActivity
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.LocalTime

/**
 * Тиковый воркер ежедневных напоминаний об уроках.
 *
 * На каждом срабатывании читает текущие настройки, проверяет:
 * - master switch + study reminders enabled
 * - сегодняшний день недели входит в `goalDaysOfWeek`
 * - текущее время не попадает в "тихие часы" (если включены)
 *
 * Если все условия выполнены — постит уведомление в канал [MemowaveNotificationChannels.STUDY_REMINDER].
 */
@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val getSettingsUseCase: GetSettingsUseCase,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val settings = getSettingsUseCase().first()

        if (!settings.notificationsEnabled || !settings.studyRemindersEnabled) {
            return Result.success()
        }
        val today = LocalDate.now().dayOfWeek.value
        if (today !in settings.goalDaysOfWeek) {
            return Result.success()
        }
        if (settings.quietHoursEnabled && isInQuietHours(LocalTime.now(), settings)) {
            return Result.success()
        }

        if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            postNotification()
        }
        return Result.success()
    }

    private fun postNotification() {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pending = PendingIntent.getActivity(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(
            context,
            MemowaveNotificationChannels.STUDY_REMINDER
        )
            .setSmallIcon(R.drawable.round_notifications_24)
            .setContentTitle(context.getString(R.string.settings_notifications_reminder_title))
            .setContentText(context.getString(R.string.settings_notifications_reminder_text))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pending)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context)
                .notify(NOTIFICATION_ID, notification)
        } catch (_: SecurityException) {
            // POST_NOTIFICATIONS revoked at runtime — silently skip.
        }
    }

    private fun isInQuietHours(now: LocalTime, s: AppSettings): Boolean {
        val start = LocalTime.of(s.quietStartHour, s.quietStartMinute)
        val end = LocalTime.of(s.quietEndHour, s.quietEndMinute)
        // If end <= start, the window crosses midnight (e.g., 22:00..07:00).
        return if (end > start) {
            now in start..end
        } else {
            now >= start || now < end
        }
    }

    private companion object {
        const val REQUEST_CODE = 1001
        const val NOTIFICATION_ID = 2001
    }
}
