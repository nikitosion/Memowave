package com.memowave.app.ui.screen.learning_shared.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.memowave.app.R
import com.memowave.app.ui.theme.MemowaveTheme

@Composable
fun CountdownContinueButton(
    text: String,
    countdownSeconds: Int?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    totalSeconds: Int = 5,
    enabled: Boolean = true
) {
    val targetProgress = countdownSeconds?.let {
        (it.toFloat() / totalSeconds).coerceIn(0f, 1f)
    } ?: 0f
    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
        label = "countdown_progress"
    )

    val a11y = countdownSeconds?.let {
        "$text. " + stringResource(R.string.flashcard_next_prep_a11y_autostart, it)
    } ?: text

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(MaterialTheme.colorScheme.onPrimaryContainer)
            .alpha(if (enabled) 1f else 0.5f)
            .clickable(enabled = enabled, onClick = onClick)
            .semantics { contentDescription = a11y }
    ) {
        if (countdownSeconds != null) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .layout { measurable, constraints ->
                        val targetWidth = (constraints.maxWidth * animatedProgress).toInt()
                        val placeable = measurable.measure(
                            constraints.copy(minWidth = targetWidth, maxWidth = targetWidth)
                        )
                        layout(placeable.width, placeable.height) {
                            placeable.placeRelative(0, 0)
                        }
                    }
                    .background(Color.White.copy(alpha = 0.22f))
            )
        }
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.W700,
                color = MaterialTheme.colorScheme.primaryContainer
            )
            if (countdownSeconds != null) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .height(20.dp)
                        .width(1.dp)
                        .background(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                        )
                )
                Text(
                    text = countdownSeconds.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.W700,
                    color = MaterialTheme.colorScheme.primaryContainer
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 400)
@Composable
private fun CountdownContinueButtonRunningPreview() {
    MemowaveTheme {
        CountdownContinueButton(
            text = "Продолжить",
            countdownSeconds = 3,
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
