package com.memowave.app.core.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.memowave.app.R

/**
 * Каналы системных уведомлений Memowave. Создаются один раз в [MemowaveApp.onCreate].
 *
 * - [STUDY_REMINDER] — ежедневные напоминания об уроках (DEFAULT)
 * - [STREAK_RISK]    — риск потерять streak (HIGH, активнее привлекает внимание)
 * - [MILESTONE]      — достижения (DEFAULT)
 */
object MemowaveNotificationChannels {
    const val STUDY_REMINDER = "memowave_study_reminder"
    const val STREAK_RISK = "memowave_streak_risk"
    const val MILESTONE = "memowave_milestone"

    fun ensureCreated(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(NotificationManager::class.java) ?: return
        manager.createNotificationChannel(
            NotificationChannel(
                STUDY_REMINDER,
                context.getString(R.string.settings_notifications_channel_study),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.settings_notifications_channel_study_desc)
            }
        )
        manager.createNotificationChannel(
            NotificationChannel(
                STREAK_RISK,
                context.getString(R.string.settings_notifications_channel_streak),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.settings_notifications_channel_streak_desc)
            }
        )
        manager.createNotificationChannel(
            NotificationChannel(
                MILESTONE,
                context.getString(R.string.settings_notifications_channel_milestone),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.settings_notifications_channel_milestone_desc)
            }
        )
    }
}
