package com.memowave.app.ui.screen.flashcard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.memowave.app.R
import com.memowave.app.domain.model.Rating
import com.memowave.app.domain.model.WordGrade
import com.memowave.app.ui.theme.LocalAppWarningColors
import com.memowave.app.ui.theme.MemowaveTheme

@Composable
fun FlashcardRatingButtons(
    isEnabled: Boolean,
    preview: Map<Rating, WordGrade>?,
    onRate: (Rating) -> Unit,
    modifier: Modifier = Modifier
) {
    val warningAccent = LocalAppWarningColors.current.accent
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RatingButton(
            label = stringResource(R.string.flashcard_rating_again),
            interval = preview?.get(Rating.Again)?.text,
            color = MaterialTheme.colorScheme.error,
            isEnabled = isEnabled,
            onClick = { onRate(Rating.Again) },
            modifier = Modifier.weight(1f)
        )
        RatingButton(
            label = stringResource(R.string.flashcard_rating_hard),
            interval = preview?.get(Rating.Hard)?.text,
            color = warningAccent,
            isEnabled = isEnabled,
            onClick = { onRate(Rating.Hard) },
            modifier = Modifier.weight(1f)
        )
        RatingButton(
            label = stringResource(R.string.flashcard_rating_good),
            interval = preview?.get(Rating.Good)?.text,
            color = MaterialTheme.colorScheme.primary,
            isEnabled = isEnabled,
            onClick = { onRate(Rating.Good) },
            modifier = Modifier.weight(1f)
        )
        RatingButton(
            label = stringResource(R.string.flashcard_rating_easy),
            interval = preview?.get(Rating.Easy)?.text,
            color = MaterialTheme.colorScheme.secondary,
            isEnabled = isEnabled,
            onClick = { onRate(Rating.Easy) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun RatingButton(
    label: String,
    interval: String?,
    color: Color,
    isEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        modifier = modifier.height(58.dp),
        onClick = onClick,
        enabled = isEnabled,
        shape = RoundedCornerShape(20.dp),
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color.copy(alpha = 0.14f),
            contentColor = color,
            disabledContainerColor = color.copy(alpha = 0.06f),
            disabledContentColor = color.copy(alpha = 0.4f)
        )
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.W700,
                maxLines = 1
            )
            if (interval != null) {
                Text(
                    text = interval,
                    style = MaterialTheme.typography.labelSmall,
                    color = color.copy(alpha = 0.75f),
                    maxLines = 1
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FlashcardRatingButtonsEnabledPreview() {
    MemowaveTheme {
        FlashcardRatingButtons(
            isEnabled = true,
            preview = mapOf(
                Rating.Again to WordGrade("Again", 0L, 0, "< 3 Min", Rating.Again),
                Rating.Hard to WordGrade("Hard", 0L, 0, "10 Min", Rating.Hard),
                Rating.Good to WordGrade("Good", 0L, 2, "2 day", Rating.Good),
                Rating.Easy to WordGrade("Easy", 0L, 6, "6 day", Rating.Easy)
            ),
            onRate = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FlashcardRatingButtonsDisabledPreview() {
    MemowaveTheme {
        FlashcardRatingButtons(
            isEnabled = false,
            preview = null,
            onRate = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
