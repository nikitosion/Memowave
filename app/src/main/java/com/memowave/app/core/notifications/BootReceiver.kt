package com.memowave.app.core.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.memowave.app.di.ApplicationScope
import com.memowave.app.domain.usecase.settings.GetSettingsUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Перепланирует ежедневные напоминания после перезагрузки устройства.
 * WorkManager сам по себе восстанавливает периодические задачи, но initial delay
 * сбрасывается — пересчитываем с новой точкой отсчёта.
 */
@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var scheduler: ReminderScheduler

    @Inject
    lateinit var getSettingsUseCase: GetSettingsUseCase

    @Inject
    @ApplicationScope
    lateinit var applicationScope: CoroutineScope

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        val pending = goAsync()
        applicationScope.launch {
            try {
                val settings = getSettingsUseCase().first()
                scheduler.schedule(settings)
            } finally {
                pending.finish()
            }
        }
    }
}
