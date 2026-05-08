package com.memowave.app.ui.screen.learning_shared.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.memowave.app.R
import com.memowave.app.ui.screen.learning_shared.LearningWordSummary

@Composable
fun ExpandableWordSummaryRow(
    summary: LearningWordSummary,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = Color.Unspecified
) {
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 90f else 0f,
        animationSpec = tween(durationMillis = 220),
        label = "expandable_word_chevron"
    )
    val resolvedContainer = if (containerColor == Color.Unspecified) {
        MaterialTheme.colorScheme.surfaceContainerLow
    } else containerColor
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(resolvedContainer)
            .clickable { onExpandedChange(!expanded) }
    ) {
        WordSummaryRow(
            summary = summary,
            trailing = {
                Icon(
                    modifier = Modifier
                        .size(22.dp)
                        .rotate(rotation),
                    painter = painterResource(R.drawable.round_chevron_right_24),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                )
            }
        )
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(animationSpec = tween(220)) +
                    fadeIn(animationSpec = tween(220)),
            exit = shrinkVertically(animationSpec = tween(180)) +
                    fadeOut(animationSpec = tween(180))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    WordProgressDeltaBlocks(delta = summary.delta)
                }
                val firstExample = summary.word.examples.firstOrNull()
                if (!firstExample.isNullOrBlank()) {
                    Text(
                        text = "“$firstExample”",
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (!summary.word.note.isNullOrBlank()) {
                    Text(
                        modifier = Modifier.padding(top = 4.dp),
                        text = summary.word.note ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.W500
                    )
                }
            }
        }
    }
}
