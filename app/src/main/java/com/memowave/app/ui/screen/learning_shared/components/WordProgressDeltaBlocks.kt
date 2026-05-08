package com.memowave.app.ui.screen.learning_shared.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.memowave.app.R
import com.memowave.app.ui.screen.learning_shared.WordProgressDelta
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun WordProgressDeltaBlocks(
    delta: WordProgressDelta?,
    modifier: Modifier = Modifier
) {
    var shown by remember { mutableStateOf<WordProgressDelta?>(null) }
    LaunchedEffect(delta) { if (delta != null) shown = delta }

    AnimatedVisibility(
        visible = delta != null,
        modifier = modifier,
        enter = fadeIn() + slideInVertically { it / 3 },
        exit = fadeOut()
    ) {
        val d = shown ?: return@AnimatedVisibility
        val daysSuffix = stringResource(R.string.flashcard_delta_days_suffix)
        Column(
            modifier = Modifier.wrapContentWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (d.wasNew) {
                LearningBadge()
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val stabTrend = trendOfDouble(d.oldStability, d.newStability)
                ParameterChangeChip(
                    label = stringResource(R.string.flashcard_delta_stability),
                    oldText = fmtDouble(d.oldStability),
                    newText = fmtDouble(d.newStability),
                    trend = stabTrend,
                    sentiment = sentimentOf(stabTrend, higherIsBetter = true),
                    isInitial = d.wasNew
                )
                val diffTrend = trendOfDouble(d.oldDifficulty, d.newDifficulty)
                ParameterChangeChip(
                    label = stringResource(R.string.flashcard_delta_difficulty),
                    oldText = fmtDouble(d.oldDifficulty),
                    newText = fmtDouble(d.newDifficulty),
                    trend = diffTrend,
                    sentiment = sentimentOf(diffTrend, higherIsBetter = false),
                    isInitial = d.wasNew
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val ivlTrend = trendOfInt(d.oldInterval, d.newInterval)
                ParameterChangeChip(
                    label = stringResource(R.string.flashcard_delta_interval),
                    oldText = "${d.oldInterval} $daysSuffix",
                    newText = "${d.newInterval} $daysSuffix",
                    trend = ivlTrend,
                    sentiment = sentimentOf(ivlTrend, higherIsBetter = true),
                    isInitial = d.wasNew
                )
                val dueTrend = trendOfDate(d.oldDueDate, d.newDueDate)
                ParameterChangeChip(
                    label = stringResource(R.string.flashcard_delta_due),
                    oldText = fmtDate(d.oldDueDate),
                    newText = fmtDate(d.newDueDate),
                    trend = dueTrend,
                    sentiment = sentimentOf(dueTrend, higherIsBetter = true),
                    isInitial = d.wasNew
                )
            }
        }
    }
}

@Composable
private fun LearningBadge() {
    val base = MaterialTheme.colorScheme.primary
    Row(
        modifier = Modifier
            .background(base.copy(alpha = 0.14f), RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.flashcard_delta_learning_badge),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.W700,
            color = base.copy(alpha = 0.95f)
        )
    }
}

@Composable
private fun ParameterChangeChip(
    label: String,
    oldText: String,
    newText: String,
    trend: Trend,
    sentiment: Sentiment,
    isInitial: Boolean = false
) {
    val base = when {
        isInitial -> MaterialTheme.colorScheme.outline
        sentiment == Sentiment.GOOD -> MaterialTheme.colorScheme.primary
        sentiment == Sentiment.BAD -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.outline
    }
    val container = base.copy(alpha = 0.14f)
    val content = base.copy(alpha = 0.95f)
    val a11y = if (isInitial) {
        "$label: $newText"
    } else {
        stringResource(R.string.flashcard_delta_a11y_change, label, oldText, newText)
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = content.copy(alpha = 0.75f)
        )
        Row(
            modifier = Modifier
                .padding(top = 2.dp)
                .background(container, RoundedCornerShape(10.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .semantics(mergeDescendants = true) { contentDescription = a11y },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (isInitial) {
                Text(
                    text = newText,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.W700,
                    color = content
                )
            } else {
                Text(
                    text = oldText,
                    style = MaterialTheme.typography.labelMedium,
                    textDecoration = TextDecoration.LineThrough,
                    color = content.copy(alpha = 0.65f)
                )
                Icon(
                    painter = painterResource(R.drawable.round_arrow_right_alt_24),
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = content.copy(alpha = 0.85f)
                )
                Text(
                    text = newText,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.W700,
                    color = content
                )
                Icon(
                    painter = painterResource(trendIconRes(trend)),
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = content
                )
            }
        }
    }
}

private enum class Trend { UP, DOWN, FLAT }
private enum class Sentiment { GOOD, BAD, NEUTRAL }

private fun sentimentOf(trend: Trend, higherIsBetter: Boolean): Sentiment = when (trend) {
    Trend.UP -> if (higherIsBetter) Sentiment.GOOD else Sentiment.BAD
    Trend.DOWN -> if (higherIsBetter) Sentiment.BAD else Sentiment.GOOD
    Trend.FLAT -> Sentiment.NEUTRAL
}

private fun trendOfDouble(old: Double, new: Double): Trend {
    val o = roundToTenth(old)
    val n = roundToTenth(new)
    return when {
        n > o -> Trend.UP
        n < o -> Trend.DOWN
        else -> Trend.FLAT
    }
}

private fun trendOfInt(old: Int, new: Int): Trend = when {
    new > old -> Trend.UP
    new < old -> Trend.DOWN
    else -> Trend.FLAT
}

private fun trendOfDate(old: LocalDateTime, new: LocalDateTime): Trend {
    val o = old.toLocalDate()
    val n = new.toLocalDate()
    return when {
        n.isAfter(o) -> Trend.UP
        n.isBefore(o) -> Trend.DOWN
        else -> Trend.FLAT
    }
}

private fun trendIconRes(t: Trend): Int = when (t) {
    Trend.UP -> R.drawable.round_trending_up_24
    Trend.DOWN -> R.drawable.round_trending_down_24
    Trend.FLAT -> R.drawable.round_trending_flat_24
}

private fun roundToTenth(v: Double): Double = Math.round(v * 10.0) / 10.0

private fun fmtDouble(v: Double): String =
    "%.1f".format(Locale.US, roundToTenth(v))

@Composable
private fun fmtDate(v: LocalDateTime): String {
    val locale = Locale.getDefault()
    val formatter = remember(locale) { DateTimeFormatter.ofPattern("d MMM", locale) }
    return v.format(formatter)
}
