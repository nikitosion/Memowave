package com.memowave.app.ui.screen.flashcard.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.memowave.app.ui.theme.MemowaveTheme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AnimatedPlayButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val onPrimaryColor = MaterialTheme.colorScheme.onPrimary

    val infiniteTransition = rememberInfiniteTransition(label = "blob")

    // Main rotation - creates the "escalator belt" effect
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // Pulsation offsets for different blob points
    val pulse1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse1"
    )

    val pulse2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse2"
    )

    val pulse3 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse3"
    )

    Box(
        modifier = modifier
            .size(220.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(220.dp)) {
            drawBlobShape(
                rotation = rotation,
                pulse1 = pulse1,
                pulse2 = pulse2,
                pulse3 = pulse3,
                color = primaryColor
            )
        }
        Text(
            text = "Начать",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.W600,
            color = onPrimaryColor
        )
    }
}

private fun DrawScope.drawBlobShape(
    rotation: Float,
    pulse1: Float,
    pulse2: Float,
    pulse3: Float,
    color: androidx.compose.ui.graphics.Color
) {
    val cx = size.width / 2
    val cy = size.height / 2
    val baseRadius = size.minDimension / 2 * 0.82f

    val pointCount = 6
    val path = Path()

    // Generate animated blob points
    val points = mutableListOf<Pair<Float, Float>>()
    for (i in 0 until pointCount) {
        val angle = rotation + (2f * PI.toFloat() / pointCount) * i

        // Each point has a slightly different pulsation
        val pulseOffset = when (i % 3) {
            0 -> sin(pulse1 + i) * baseRadius * 0.12f
            1 -> sin(pulse2 + i * 0.7f) * baseRadius * 0.10f
            else -> sin(pulse3 + i * 1.3f) * baseRadius * 0.14f
        }
        val r = baseRadius + pulseOffset

        val x = cx + r * cos(angle)
        val y = cy + r * sin(angle)
        points.add(Pair(x, y))
    }

    // Draw smooth blob using cubic bezier curves through the points
    if (points.isNotEmpty()) {
        path.moveTo(
            (points.last().first + points[0].first) / 2,
            (points.last().second + points[0].second) / 2
        )

        for (i in points.indices) {
            val current = points[i]
            val next = points[(i + 1) % points.size]
            val midX = (current.first + next.first) / 2
            val midY = (current.second + next.second) / 2
            path.quadraticTo(current.first, current.second, midX, midY)
        }
        path.close()
    }

    drawPath(path = path, color = color)
}

@Preview(showBackground = true)
@Composable
private fun AnimatedPlayButtonPreview() {
    MemowaveTheme {
        Box(
            modifier = Modifier.size(260.dp),
            contentAlignment = Alignment.Center
        ) {
            AnimatedPlayButton(onClick = {})
        }
    }
}
