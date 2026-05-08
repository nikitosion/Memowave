package com.memowave.app.domain.algorithm

import com.memowave.app.di.ApplicationScope
import com.memowave.app.domain.model.settings.AppSettings
import com.memowave.app.domain.repository.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Singleton factory holding the latest FSRS instance built from user settings.
 *
 * Subscribes once to [SettingsRepository] and rebuilds the cached [FSRS] when any
 * of the four user-tunable knobs (retention, max interval, easy bonus, hard penalty)
 * change. Consumers call [current] to obtain the up-to-date instance.
 */
@Singleton
class FSRSFactory @Inject constructor(
    settingsRepository: SettingsRepository,
    @ApplicationScope applicationScope: CoroutineScope
) {
    @Volatile
    private var cached: FSRS = build(
        requestRetention = FSRSConfig.REQUEST_RETENTION,
        maxInterval = DEFAULT_MAX_INTERVAL,
        easyBonus = FSRSConfig.DEFAULT_PARAMS[16],
        hardPenalty = FSRSConfig.DEFAULT_PARAMS[15]
    )

    init {
        applicationScope.launch {
            settingsRepository.getSettings()
                .distinctUntilChanged { old, new -> old.fsrsKnobsEqualTo(new) }
                .collect { settings ->
                    cached = build(
                        requestRetention = settings.fsrsRequestRetention,
                        maxInterval = settings.fsrsMaximumInterval,
                        easyBonus = settings.fsrsEasyBonus,
                        hardPenalty = settings.fsrsHardPenalty
                    )
                }
        }
    }

    fun current(): FSRS = cached

    private fun build(
        requestRetention: Double,
        maxInterval: Int,
        easyBonus: Double,
        hardPenalty: Double
    ): FSRS {
        val tunedParams = FSRSConfig.DEFAULT_PARAMS.toMutableList().apply {
            this[15] = hardPenalty
            this[16] = easyBonus
        }
        return FSRS(
            requestRetention = requestRetention,
            params = tunedParams,
            maxInterval = maxInterval
        )
    }

    private fun AppSettings.fsrsKnobsEqualTo(other: AppSettings): Boolean =
        fsrsRequestRetention == other.fsrsRequestRetention &&
            fsrsMaximumInterval == other.fsrsMaximumInterval &&
            fsrsEasyBonus == other.fsrsEasyBonus &&
            fsrsHardPenalty == other.fsrsHardPenalty

    private companion object {
        const val DEFAULT_MAX_INTERVAL = 365
    }
}
