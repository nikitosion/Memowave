package com.memowave.app.ui.screen.learning_shared.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.memowave.app.R
import com.memowave.app.ui.screen.learning_shared.LearningWordSummary
import com.memowave.app.ui.screen.learning_shared.WordProgressDelta

@Composable
fun WordSummaryRow(
    summary: LearningWordSummary,
    modifier: Modifier = Modifier,
    trailing: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatusDot(isCorrect = summary.isCorrect)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = summary.word.original,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.W600,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = summary.word.translation,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        if (summary.delta.wasNew) {
            NewWordBadgeCompact()
        } else {
            CompactIntervalDeltaChip(delta = summary.delta)
        }
        trailing?.invoke()
    }
}

@Composable
private fun NewWordBadgeCompact() {
    Text(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 10.dp, vertical = 4.dp),
        text = stringResource(R.string.flashcard_badge_new),
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.W700,
        color = MaterialTheme.colorScheme.primaryContainer,
        maxLines = 1
    )
}

@Composable
private fun StatusDot(isCorrect: Boolean) {
    val color = if (isCorrect) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.error
    Box(
        modifier = Modifier
            .size(22.dp)
            .background(color.copy(alpha = 0.14f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier.size(14.dp),
            painter = painterResource(
                if (isCorrect) R.drawable.round_check_24 else R.drawable.round_close_24
            ),
            contentDescription = null,
            tint = color
        )
    }
}

@Composable
private fun CompactIntervalDeltaChip(delta: WordProgressDelta) {
    val daysSuffix = stringResource(R.string.flashcard_delta_days_suffix)
    val trend = when {
        delta.newInterval > delta.oldInterval -> CompactTrend.UP
        delta.newInterval < delta.oldInterval -> CompactTrend.DOWN
        else -> CompactTrend.FLAT
    }
    val baseColor: Color = when (trend) {
        CompactTrend.UP -> MaterialTheme.colorScheme.primary
        CompactTrend.DOWN -> MaterialTheme.colorScheme.error
        CompactTrend.FLAT -> MaterialTheme.colorScheme.outline
    }
    Row(
        modifier = Modifier
            .background(baseColor.copy(alpha = 0.14f), RoundedCornerShape(10.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = "${delta.oldInterval}$daysSuffix",
            style = MaterialTheme.typography.labelSmall,
            color = baseColor.copy(alpha = 0.65f)
        )
        Icon(
            modifier = Modifier.size(12.dp),
            painter = painterResource(R.drawable.round_arrow_right_alt_24),
            contentDescription = null,
            tint = baseColor.copy(alpha = 0.7f)
        )
        Text(
            text = "${delta.newInterval}$daysSuffix",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.W700,
            color = baseColor
        )
        Icon(
            modifier = Modifier.size(12.dp),
            painter = painterResource(
                when (trend) {
                    CompactTrend.UP -> R.drawable.round_trending_up_24
                    CompactTrend.DOWN -> R.drawable.round_trending_down_24
                    CompactTrend.FLAT -> R.drawable.round_trending_flat_24
                }
            ),
            contentDescription = null,
            tint = baseColor
        )
    }
}

private enum class CompactTrend { UP, DOWN, FLAT }
