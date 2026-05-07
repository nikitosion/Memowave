package com.memowave.app.ui.screen.settings.algorithm

import com.memowave.app.domain.algorithm.FSRSConfig

data class AlgorithmUiState(
    val requestRetention: Float = FSRSConfig.REQUEST_RETENTION.toFloat(),
    val maximumInterval: Int = 365,
    val easyBonus: Float = FSRSConfig.DEFAULT_PARAMS[16].toFloat(),
    val hardPenalty: Float = FSRSConfig.DEFAULT_PARAMS[15].toFloat(),
    val resetDialogShown: Boolean = false
)
