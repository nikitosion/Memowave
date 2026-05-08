package com.memowave.app.ui.screen.settings.algorithm.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.memowave.app.R
import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * Наглядный показ роста интервалов при заданной целевой запоминаемости.
 *
 * Строит последовательность "ожидаемых интервалов" для нескольких стабильностей
 * (типичные точки на кривой обучения) — пользователь сразу видит, как
 * параметр увеличивает или уменьшает шаг между повторами.
 */
@Composable
fun IntervalProjectionStrip(
    retention: Float,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val intervals = remember(retention) { previewIntervals(retention.toDouble()) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = stringResource(R.string.settings_algorithm_retention_intervals_label),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            intervals.forEachIndexed { index, days ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = formatDays(context, days),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.W600,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
                if (index < intervals.lastIndex) {
                    Text(
                        text = "→",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
                    )
                }
            }
        }
    }
}

private val DEFAULT_DECAY = -0.1542
private val SAMPLE_STABILITIES = listOf(2.0, 10.0, 30.0, 90.0)

private fun previewIntervals(retention: Double): List<Int> {
    val decay = DEFAULT_DECAY
    val factor = 0.9.pow(1.0 / decay) - 1
    val multiplier = (retention.pow(1.0 / decay) - 1) / factor
    return SAMPLE_STABILITIES.map { stability ->
        (stability * multiplier).roundToInt().coerceAtLeast(1)
    }
}

private fun formatDays(context: android.content.Context, days: Int): String {
    return if (days >= 365) {
        context.getString(R.string.settings_algorithm_years_format, days / 365f)
    } else {
        context.getString(R.string.settings_algorithm_days_format, days)
    }
}
