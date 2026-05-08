package com.memowave.app.ui.screen.flashcard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.memowave.app.R
import com.memowave.app.ui.common.MemowaveTopBar
import com.memowave.app.ui.theme.MemowaveTheme

@Composable
fun FlashcardTopBar(
    progress: String,
    totalXp: Int,
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    MemowaveTopBar(
        modifier = modifier,
        onBackClick = onBackClick,
        title = "$progress \u2022 Изучение",
        bottomContent = {
            LinearProgressIndicator(
                progress = { progress.split("/").let { it[0].toFloat() / it[1].toFloat() } },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp)
                    .height(8.dp),
                color = ProgressIndicatorDefaults.linearColor,
                trackColor = ProgressIndicatorDefaults.linearTrackColor,
                strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
            )
        }
    ) {
        // XP badge
        Row(
            modifier = Modifier
                .height(48.dp)
                .background(
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.round_stars_24),
                contentDescription = null,
                modifier = Modifier.size(22.dp),
                tint = MaterialTheme.colorScheme.primaryContainer
            )
            Text(
                text = "$totalXp XP",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.W700,
                color = MaterialTheme.colorScheme.primaryContainer
            )
        }

        // Settings button
        IconButton(
            modifier = Modifier
                .padding(end = 8.dp)
                .height(48.dp)
                .background(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = CircleShape
                ),
            onClick = onSettingsClick
        ) {
            Icon(
                modifier = Modifier.size(22.dp),
                painter = painterResource(R.drawable.round_settings_24),
                contentDescription = "Настройки",
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FlashcardTopBarPreview() {
    MemowaveTheme {
        FlashcardTopBar(
            progress = "3/15",
            totalXp = 50,
            onBackClick = {},
            onSettingsClick = {}
        )
    }
}
