package com.memowave.app.ui.screen.quiz.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.memowave.app.domain.model.Word

@Composable
fun QuizGameContent(
    word: Word,
    options: List<String>,
    correctOptionIndex: Int,
    selectedOptionIndex: Int?,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp, bottom = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = word.original,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.W700,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            options.forEachIndexed { index, label ->
                val state = when {
                    selectedOptionIndex == null -> QuizOptionState.IDLE
                    index == selectedOptionIndex && index == correctOptionIndex -> QuizOptionState.CORRECT
                    index == selectedOptionIndex -> QuizOptionState.WRONG
                    index == correctOptionIndex -> QuizOptionState.NEUTRAL_CORRECT_REVEAL
                    else -> QuizOptionState.IDLE
                }
                QuizOptionButton(
                    label = label,
                    state = state,
                    enabled = selectedOptionIndex == null,
                    onClick = { onSelect(index) }
                )
            }
        }
        Spacer(Modifier.height(12.dp))
    }
}
