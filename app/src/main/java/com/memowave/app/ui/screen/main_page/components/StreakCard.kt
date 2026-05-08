package com.memowave.app.ui.screen.main_page.components

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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.memowave.app.R
import com.memowave.app.domain.model.streak.StreakState
import com.memowave.app.ui.theme.LocalAppWarningColors
import com.memowave.app.ui.theme.MemowaveTheme

@Composable
fun StreakCard(
    state: StreakState,
    modifier: Modifier = Modifier,
) {
    val warning = LocalAppWarningColors.current
    val progress = if (state.dailyTarget > 0) {
        (state.wordsCompletedToday.toFloat() / state.dailyTarget).coerceIn(0f, 1f)
    } else 0f

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(warning.container)
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = warning.accent.copy(alpha = 0.22f),
                        shape = CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    modifier = Modifier.size(32.dp),
                    painter = painterResource(R.drawable.round_fire_24),
                    contentDescription = null,
                    tint = warning.accent,
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = pluralStringResource(
                        R.plurals.streak_card_days,
                        state.currentStreak,
                        state.currentStreak,
                    ),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.W700,
                    color = warning.onContainer,
                )
                Text(
                    text = if (state.longestStreak > 0) {
                        stringResource(R.string.streak_card_best, state.longestStreak)
                    } else {
                        stringResource(R.string.streak_card_subtitle_empty)
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = warning.onContainer.copy(alpha = 0.78f),
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = if (state.isTodayCompleted) {
                        stringResource(R.string.streak_card_today_done)
                    } else {
                        stringResource(R.string.streak_card_today_progress)
                    },
                    style = MaterialTheme.typography.labelMedium,
                    color = warning.onContainer.copy(alpha = 0.85f),
                )
                Text(
                    text = "${state.wordsCompletedToday}/${state.dailyTarget}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.W600,
                    color = warning.onContainer,
                )
            }
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(50)),
                color = warning.accent,
                trackColor = warning.accent.copy(alpha = 0.22f),
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 400)
@Composable
private fun StreakCardActivePreview() {
    MemowaveTheme {
        StreakCard(
            state = StreakState(
                currentStreak = 7,
                longestStreak = 12,
                dailyTarget = 10,
                wordsCompletedToday = 4,
                isAlive = true,
            ),
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(showBackground = true, widthDp = 400)
@Composable
private fun StreakCardEmptyPreview() {
    MemowaveTheme {
        StreakCard(
            state = StreakState.EMPTY.copy(dailyTarget = 5),
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(showBackground = true, widthDp = 400)
@Composable
private fun StreakCardCompletedPreview() {
    MemowaveTheme {
        StreakCard(
            state = StreakState(
                currentStreak = 3,
                longestStreak = 9,
                dailyTarget = 5,
                wordsCompletedToday = 5,
                isAlive = true,
            ),
            modifier = Modifier.padding(16.dp),
        )
    }
}
