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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.memowave.app.R
import com.memowave.app.ui.theme.MemowaveTheme

@Composable
fun FlashcardSummaryContent(
    correctCount: Int,
    wrongCount: Int,
    totalXp: Int,
    totalWords: Int,
    onPlayAgain: () -> Unit,
    onGoBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val percentage = if (totalWords > 0) correctCount.toFloat() / totalWords else 0f

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Результат",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.W600
        )

        // Progress circle
        Box(
            modifier = Modifier
                .padding(top = 32.dp)
                .size(160.dp),
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

        // Stats row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp),
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

        // Action buttons
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp),
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

@Preview(showBackground = true, device = "spec:height=900dp,width=411dp")
@Composable
private fun FlashcardSummaryContentPreview() {
    MemowaveTheme {
        FlashcardSummaryContent(
            correctCount = 12,
            wrongCount = 3,
            totalXp = 300,
            totalWords = 15,
            onPlayAgain = {},
            onGoBack = {}
        )
    }
}
