package com.memowave.app.ui.screen.translation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.unit.dp
import com.memowave.app.R
import com.memowave.app.domain.model.Rating
import com.memowave.app.ui.screen.translation.TranslationCheckResult

@Composable
fun TranslationFeedbackCard(
    result: TranslationCheckResult,
    modifier: Modifier = Modifier
) {
    val (containerColor, accentColor, iconRes, headlineRes) = when (result.rating) {
        Rating.Easy, Rating.Good -> Quad(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
            MaterialTheme.colorScheme.primary,
            R.drawable.round_check_24,
            R.string.translation_correct
        )
        Rating.Hard -> Quad(
            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.18f),
            MaterialTheme.colorScheme.tertiary,
            R.drawable.round_check_24,
            R.string.translation_close_label
        )
        Rating.Again -> Quad(
            MaterialTheme.colorScheme.error.copy(alpha = 0.14f),
            MaterialTheme.colorScheme.error,
            R.drawable.round_close_24,
            R.string.translation_wrong_label
        )
    }
    val similarityPct = (result.similarity * 100).toInt().coerceIn(0, 100)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(containerColor, RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                modifier = Modifier
                    .size(28.dp)
                    .background(accentColor.copy(alpha = 0.2f), CircleShape)
                    .padding(4.dp),
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = accentColor
            )
            Text(
                text = stringResource(headlineRes),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.W700,
                color = accentColor
            )
            Text(
                modifier = Modifier.weight(1f),
                text = "",
            )
            Text(
                text = "$similarityPct%",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.W600,
                color = accentColor
            )
        }
        if (result.rating != Rating.Easy && result.rating != Rating.Good) {
            Text(
                text = stringResource(R.string.translation_correct_answer_was, result.correctAnswer),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

private data class Quad(
    val container: Color,
    val accent: Color,
    val iconRes: Int,
    val headlineRes: Int
)
