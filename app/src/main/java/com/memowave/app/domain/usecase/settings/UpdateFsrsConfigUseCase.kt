package com.memowave.app.domain.usecase.settings

import com.memowave.app.domain.repository.SettingsRepository
import javax.inject.Inject

/**
 * Granular FSRS knob update — pass only the fields you want to change.
 * Each non-null field is delegated to the corresponding [SettingsRepository] setter.
 * This keeps slider drag handlers from issuing redundant DataStore writes.
 */
class UpdateFsrsConfigUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(
        requestRetention: Double? = null,
        maximumInterval: Int? = null,
        easyBonus: Double? = null,
        hardPenalty: Double? = null
    ) {
        if (requestRetention != null) repository.setFsrsRequestRetention(requestRetention)
        if (maximumInterval != null) repository.setFsrsMaximumInterval(maximumInterval)
        if (easyBonus != null) repository.setFsrsEasyBonus(easyBonus)
        if (hardPenalty != null) repository.setFsrsHardPenalty(hardPenalty)
    }
}
