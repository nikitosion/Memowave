package com.memowave.app.ui.screen.flashcard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.memowave.app.R
import com.memowave.app.ui.theme.LocalAppWarningColors
import com.memowave.app.ui.theme.MemowaveTheme

/**
 * Banner card showing how many words remain to save the current learning streak.
 * Pure presentation — pass in the count, the component owns no streak logic.
 *
 * Uses the app-wide warning palette (yellow-amber) from [LocalAppWarningColors],
 * giving a warm "fire" feel that pairs with the flame icon and reads as a
 * non-blocking reminder rather than an error or a regular surface card.
 */
@Composable
fun StreakProgressCard(
    wordsRemaining: Int,
    modifier: Modifier = Modifier
) {
    val warning = LocalAppWarningColors.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(warning.container)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = warning.accent.copy(alpha = 0.22f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(R.drawable.round_fire_24),
                contentDescription = null,
                tint = warning.accent
            )
        }
        Text(
            modifier = Modifier.weight(1f),
            text = pluralStringResource(
                R.plurals.flashcard_summary_streak_remaining,
                wordsRemaining,
                wordsRemaining
            ),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.W600,
            color = warning.onContainer
        )
    }
}

@Preview(showBackground = true, widthDp = 400)
@Composable
private fun StreakProgressCardPreview() {
    MemowaveTheme {
        StreakProgressCard(wordsRemaining = 3, modifier = Modifier.padding(16.dp))
    }
}

@Preview(showBackground = true, widthDp = 400)
@Composable
private fun StreakProgressCardOnePreview() {
    MemowaveTheme {
        StreakProgressCard(wordsRemaining = 1, modifier = Modifier.padding(16.dp))
    }
}
