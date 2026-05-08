package com.memowave.app.ui.screen.quiz.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.memowave.app.R

@Composable
fun QuizTimerBar(
    remainingMillis: Long,
    totalMillis: Long,
    streak: Int,
    modifier: Modifier = Modifier
) {
    val progress = if (totalMillis > 0) (remainingMillis.toFloat() / totalMillis).coerceIn(0f, 1f) else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(200, easing = LinearEasing),
        label = "quiz_timer"
    )
    val secondsLeft = ((remainingMillis + 999) / 1000).toInt().coerceAtLeast(0)
    val barColor = when {
        progress > 0.5f -> MaterialTheme.colorScheme.primary
        progress > 0.2f -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.error
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .layout { measurable, constraints ->
                        val targetWidth = (constraints.maxWidth * animatedProgress).toInt()
                        val placeable = measurable.measure(
                            constraints.copy(minWidth = targetWidth, maxWidth = targetWidth)
                        )
                        layout(placeable.width, placeable.height) {
                            placeable.placeRelative(0, 0)
                        }
                    }
                    .background(barColor)
            )
        }
        Text(
            text = stringResource(R.string.quiz_time_left, secondsLeft),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.W700,
            color = barColor
        )
        if (streak > 1) {
            Text(
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.tertiaryContainer,
                        RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                text = stringResource(R.string.quiz_streak, streak),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.W700,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }
    }
}
