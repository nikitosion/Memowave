package com.memowave.app.ui.screen.quiz.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

enum class QuizOptionState { IDLE, CORRECT, WRONG, NEUTRAL_CORRECT_REVEAL }

@Composable
fun QuizOptionButton(
    label: String,
    state: QuizOptionState,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val targetContainer = when (state) {
        QuizOptionState.IDLE -> MaterialTheme.colorScheme.surfaceContainerLow
        QuizOptionState.CORRECT, QuizOptionState.NEUTRAL_CORRECT_REVEAL ->
            MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
        QuizOptionState.WRONG -> MaterialTheme.colorScheme.error.copy(alpha = 0.16f)
    }
    val targetContent = when (state) {
        QuizOptionState.IDLE -> MaterialTheme.colorScheme.onSurface
        QuizOptionState.CORRECT, QuizOptionState.NEUTRAL_CORRECT_REVEAL ->
            MaterialTheme.colorScheme.primary
        QuizOptionState.WRONG -> MaterialTheme.colorScheme.error
    }
    val borderColor: Color = when (state) {
        QuizOptionState.IDLE -> Color.Transparent
        QuizOptionState.CORRECT, QuizOptionState.NEUTRAL_CORRECT_REVEAL ->
            MaterialTheme.colorScheme.primary
        QuizOptionState.WRONG -> MaterialTheme.colorScheme.error
    }
    val animatedContainer by animateColorAsState(targetContainer, tween(180), label = "qbg")
    val animatedContent by animateColorAsState(targetContent, tween(180), label = "qfg")

    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(animatedContainer)
            .border(
                width = if (state == QuizOptionState.IDLE) 0.dp else 2.dp,
                color = borderColor,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.W600,
            color = animatedContent,
            textAlign = TextAlign.Start
        )
    }
}
