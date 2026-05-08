package com.memowave.app.domain.usecase.streak

import com.memowave.app.domain.model.settings.AppSettings
import com.memowave.app.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import javax.inject.Inject
import kotlin.math.max

/**
 * Регистрирует одно успешное оценивание слова в счётчике дневного прогресса.
 *
 * Если день сменился относительно `lastActivityDate`, дневной счётчик предварительно
 * сбрасывается. При первом за сегодня достижении дневной цели инкрементится стрик
 * (с учётом непрерывности — `lastStreakDate == today.minusDays(1)`); иначе стрик
 * обнуляется до 1. Если цель уже была закрыта сегодня — счётчик инкрементится,
 * но стрик уже не растёт.
 */
class RecordWordReviewUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(today: LocalDate = LocalDate.now()) {
        val settings = repository.getSettings().first()
        val daily = dailyTargetFrom(settings)
        val todayIso = today.toString()

        val lastActivityIso = settings.lastActivityDate
        val effectiveWordsToday = if (lastActivityIso == todayIso) settings.wordsCompletedToday else 0
        val newWordsToday = effectiveWordsToday + 1

        val justHitTarget = daily > 0 &&
            effectiveWordsToday < daily &&
            newWordsToday >= daily

        val lastStreakIso = settings.lastStreakDate
        val (newCurrent, newLongest, newLastStreak) = if (justHitTarget) {
            val keepStreak = lastStreakIso == today.minusDays(1).toString()
            val nc = if (keepStreak) settings.currentStreak + 1 else 1
            Triple(nc, max(settings.longestStreak, nc), todayIso)
        } else {
            Triple(settings.currentStreak, settings.longestStreak, lastStreakIso)
        }

        repository.setStreakState(
            currentStreak = newCurrent,
            longestStreak = newLongest,
            lastStreakDate = newLastStreak,
            wordsCompletedToday = newWordsToday,
            lastActivityDate = todayIso,
        )
    }
}

internal fun dailyTargetFrom(settings: AppSettings): Int {
    val days = settings.goalDaysOfWeek.size
    val divisor = if (days <= 0) 7 else days
    return ((settings.weeklyWordGoal + divisor - 1) / divisor).coerceAtLeast(1)
}
