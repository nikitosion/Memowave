package com.memowave.app.domain.usecase.streak

import com.memowave.app.domain.model.streak.StreakState
import com.memowave.app.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

/**
 * Реактивно отдаёт текущее состояние стрика для UI.
 *
 * Делает «протухание дня» только для отображения: если в DataStore лежит активность
 * прошлого дня, дневной счётчик нормализуется к 0; если стрик старше чем «вчера»,
 * он считается мёртвым (`isAlive = false`). Сама запись делается на старте приложения
 * (см. `MemowaveApp.observeStreakStaleness`) и при следующем оценивании — этот use case
 * её не пишет.
 */
class GetStreakStateUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    operator fun invoke(today: () -> LocalDate = LocalDate::now): Flow<StreakState> =
        repository.getSettings().map { settings ->
            val now = today()
            val todayIso = now.toString()
            val yesterdayIso = now.minusDays(1).toString()

            val daily = dailyTargetFrom(settings)
            val wordsToday = if (settings.lastActivityDate == todayIso) {
                settings.wordsCompletedToday
            } else 0

            val lastStreakIso = settings.lastStreakDate
            val isAlive = lastStreakIso == todayIso || lastStreakIso == yesterdayIso

            // Если стрик протух (lastStreakDate < вчера), показываем 0 в UI до тех пор
            // пока ролловер на старте приложения не запишет это в хранилище.
            val displayedCurrent = if (isAlive) settings.currentStreak else 0

            StreakState(
                currentStreak = displayedCurrent,
                longestStreak = settings.longestStreak,
                dailyTarget = daily,
                wordsCompletedToday = wordsToday,
                isAlive = isAlive,
            )
        }
}
