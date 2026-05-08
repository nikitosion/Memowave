package com.memowave.app.ui.screen.flashcard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.memowave.app.R
import com.memowave.app.domain.algorithm.CardPhase
import com.memowave.app.domain.model.Word
import com.memowave.app.ui.screen.flashcard.FlashcardWordSummary
import com.memowave.app.ui.screen.flashcard.WordProgressDelta
import com.memowave.app.ui.theme.MemowaveTheme
import java.time.LocalDateTime

private const val INLINE_WORDS_PREVIEW_COUNT = 3

@Composable
fun FlashcardSummaryContent(
    correctCount: Int,
    wrongCount: Int,
    totalXp: Int,
    totalWords: Int,
    summaries: List<FlashcardWordSummary>,
    streakWordsRemaining: Int,
    onPlayAgain: () -> Unit,
    onGoBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val percentage = if (totalWords > 0) correctCount.toFloat() / totalWords else 0f
    var showAllSheet by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .padding(top = 16.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Результат",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.W600
        )

        Box(
            modifier = Modifier
                .padding(top = 24.dp)
                .size(140.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = { percentage },
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                strokeWidth = 12.dp
            )
            Text(
                text = "${(percentage * 100).toInt()}%",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.W700,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(
                icon = R.drawable.round_check_24,
                value = correctCount.toString(),
                label = "Правильно",
                color = MaterialTheme.colorScheme.primary
            )
            StatItem(
                icon = R.drawable.round_close_24,
                value = wrongCount.toString(),
                label = "Неправильно",
                color = MaterialTheme.colorScheme.error
            )
            StatItem(
                icon = R.drawable.round_stars_24,
                value = "$totalXp XP",
                label = "Заработано",
                color = MaterialTheme.colorScheme.tertiary
            )
        }

        if (summaries.isNotEmpty()) {
            SessionWordsSection(
                summaries = summaries,
                onShowAllClick = { showAllSheet = true },
                modifier = Modifier.padding(top = 24.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                onClick = onPlayAgain,
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "Продолжить",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.W700
                )
            }

            OutlinedButton(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
                    .height(48.dp),
                onClick = onGoBack,
                shape = RoundedCornerShape(30.dp)
            ) {
                Text(
                    text = "На главную",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                    fontWeight = FontWeight.W500
                )
            }
        }

        if (streakWordsRemaining > 0) {
            StreakProgressCard(
                wordsRemaining = streakWordsRemaining,
                modifier = Modifier.padding(top = 24.dp)
            )
        }
    }

    if (showAllSheet) {
        WordSummaryListSheet(
            summaries = summaries,
            onDismiss = { showAllSheet = false }
        )
    }
}

@Composable
private fun SessionWordsSection(
    summaries: List<FlashcardWordSummary>,
    onShowAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val visible = summaries.take(INLINE_WORDS_PREVIEW_COUNT)
    val hasMore = summaries.size > INLINE_WORDS_PREVIEW_COUNT
    // Track which inline row is expanded; only one open at a time, like the
    // bottom sheet variant — keeps the section compact.
    var expandedId by remember { mutableStateOf<Long?>(null) }
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.flashcard_summary_session_words_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.W700,
            color = MaterialTheme.colorScheme.onSurface
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
        ) {
            visible.forEachIndexed { index, summary ->
                ExpandableWordSummaryRow(
                    summary = summary,
                    expanded = expandedId == summary.word.id,
                    onExpandedChange = { wantOpen ->
                        expandedId = if (wantOpen) summary.word.id else null
                    },
                    // Inline rows live inside one shared card — turn off the
                    // per-row background so they read as a single grouped list.
                    containerColor = Color.Transparent
                )
                if (index < visible.lastIndex) {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                    )
                }
            }
        }
        if (hasMore) {
            OutlinedButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                onClick = onShowAllClick,
                shape = RoundedCornerShape(24.dp)
            ) {
                Text(
                    text = stringResource(
                        R.string.flashcard_summary_show_all_button,
                        summaries.size
                    ),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.W600
                )
            }
        }
    }
}

@Composable
private fun StatItem(
    icon: Int,
    value: String,
    label: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            modifier = Modifier
                .background(
                    color = color.copy(alpha = 0.1f),
                    shape = CircleShape
                )
                .padding(10.dp)
                .size(24.dp),
            painter = painterResource(icon),
            contentDescription = null,
            tint = color
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.W700,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline,
            textAlign = TextAlign.Center
        )
    }
}

// --- Previews ---

private fun sampleSummary(
    id: Long,
    original: String,
    translation: String,
    isCorrect: Boolean,
    wasNew: Boolean,
    oldI: Int,
    newI: Int
) = FlashcardWordSummary(
    word = Word(
        id = id,
        original = original,
        translation = translation,
        phase = if (wasNew) CardPhase.Added.value else CardPhase.Review.value,
        examples = listOf("Sample sentence using $original.")
    ),
    isCorrect = isCorrect,
    delta = WordProgressDelta(
        wasNew = wasNew,
        oldStability = 2.5, newStability = 4.0,
        oldDifficulty = 4.0, newDifficulty = 4.2,
        oldInterval = oldI, newInterval = newI,
        oldDueDate = LocalDateTime.of(2026, 5, 1, 12, 0),
        newDueDate = LocalDateTime.of(2026, 5, 8, 12, 0)
    )
)

@Preview(showBackground = true, device = "spec:height=900dp,width=411dp")
@Composable
private fun FlashcardSummaryContentPreview() {
    MemowaveTheme {
        FlashcardSummaryContent(
            correctCount = 12,
            wrongCount = 3,
            totalXp = 300,
            totalWords = 15,
            summaries = listOf(
                sampleSummary(1L, "ephemeral", "мимолётный", true, false, 1, 6),
                sampleSummary(2L, "serendipity", "счастливая случайность", true, true, 0, 0),
                sampleSummary(3L, "ubiquitous", "повсеместный", false, false, 12, 1),
                sampleSummary(4L, "quintessential", "типичный", true, false, 4, 10),
                sampleSummary(5L, "voracious", "ненасытный", true, false, 2, 5)
            ),
            streakWordsRemaining = 3,
            onPlayAgain = {},
            onGoBack = {}
        )
    }
}
