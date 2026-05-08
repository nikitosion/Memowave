package com.memowave.app.ui.screen.flashcard.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.memowave.app.ui.screen.flashcard.FlashcardGameMode
import com.memowave.app.ui.theme.MemowaveTheme

@Composable
fun FlashcardGameModeSwitcher(
    modifier: Modifier = Modifier,
    selectedMode: FlashcardGameMode = FlashcardGameMode.RECALL,
    onModeSelected: (FlashcardGameMode) -> Unit = {}
) {
    val colorScheme = MaterialTheme.colorScheme

    val leftSelected = selectedMode == FlashcardGameMode.RECALL
    val rightSelected = selectedMode == FlashcardGameMode.NUMBERED

    val dimFill = colorScheme.onSurface.copy(alpha = 0.04f)
    val dimContent = colorScheme.onSurface.copy(alpha = 0.19f)

    val activeFill = colorScheme.onSecondaryContainer.copy(alpha = 0.08f)
    val activeContent = colorScheme.onSecondaryContainer.copy(alpha = 0.8f)

    val leftPanelBg by animateColorAsState(
        if (leftSelected) colorScheme.secondaryContainer else colorScheme.surfaceContainerHigh
    )
    val leftLabelColor by animateColorAsState(if (leftSelected) activeContent else dimContent)
    val leftPillOpacity by animateFloatAsState(if (leftSelected) 1f else 0.35f)

    val rightPanelBg by animateColorAsState(
        if (rightSelected) colorScheme.secondaryContainer else colorScheme.surfaceContainerHigh
    )
    val rightChipBg by animateColorAsState(if (rightSelected) activeFill else dimFill)
    val rightChipContent by animateColorAsState(if (rightSelected) activeContent else dimContent)

    val pillBaseColor = colorScheme.onSecondaryContainer

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        color = leftPanelBg,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .clickable { onModeSelected(FlashcardGameMode.RECALL) }
                    .padding(horizontal = 10.dp, vertical = 10.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    RatingPreviewPill(
                        letter = "З",
                        color = pillBaseColor,
                        tone = 0.30f,
                        selectionOpacity = leftPillOpacity,
                        modifier = Modifier.weight(1f)
                    )
                    RatingPreviewPill(
                        letter = "С",
                        color = pillBaseColor,
                        tone = 0.55f,
                        selectionOpacity = leftPillOpacity,
                        modifier = Modifier.weight(1f)
                    )
                    RatingPreviewPill(
                        letter = "Н",
                        color = pillBaseColor,
                        tone = 0.80f,
                        selectionOpacity = leftPillOpacity,
                        modifier = Modifier.weight(1f)
                    )
                    RatingPreviewPill(
                        letter = "Л",
                        color = pillBaseColor,
                        tone = 1.00f,
                        selectionOpacity = leftPillOpacity,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Text(
                text = "Сложность",
                style = MaterialTheme.typography.labelMedium,
                color = leftLabelColor
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        color = rightPanelBg,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .clickable { onModeSelected(FlashcardGameMode.NUMBERED) }
                    .padding(
                        vertical = 8.dp,
                        horizontal = 16.dp
                    )
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.weight(1f)) {
                        NumberedPreviewChip(
                            number = 1,
                            containerColor = rightChipBg,
                            contentColor = rightChipContent,
                            modifier = Modifier.weight(1f)
                        )
                        NumberedPreviewChip(
                            number = 2,
                            containerColor = rightChipBg,
                            contentColor = rightChipContent,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(modifier = Modifier.weight(1f)) {
                        NumberedPreviewChip(
                            number = 3,
                            containerColor = rightChipBg,
                            contentColor = rightChipContent,
                            modifier = Modifier.weight(1f)
                        )
                        NumberedPreviewChip(
                            number = 4,
                            containerColor = rightChipBg,
                            contentColor = rightChipContent,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            Text(
                text = "Выбор из 4",
                style = MaterialTheme.typography.labelMedium,
                color = rightChipContent
            )
        }
    }
}

@Composable
private fun RatingPreviewPill(
    letter: String,
    color: Color,
    tone: Float,
    selectionOpacity: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(40.dp)
            .background(
                color = color.copy(alpha = (0.08f + 0.18f * tone) * selectionOpacity),
                shape = RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = letter,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.W700,
            color = color.copy(alpha = (0.45f + 0.55f * tone) * selectionOpacity)
        )
    }
}

@Composable
private fun NumberedPreviewChip(
    number: Int,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(4.dp)
            .background(
                color = containerColor,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = number.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = contentColor
        )
    }
}

@Composable
@Preview
fun FlashcardGameModeSwitcherBinaryPreview() {
    MemowaveTheme {
        var selected by remember { mutableStateOf(FlashcardGameMode.RECALL) }
        FlashcardGameModeSwitcher(
            selectedMode = selected,
            onModeSelected = { selected = it }
        )
    }
}

@Composable
@Preview
fun FlashcardGameModeSwitcherNumberedPreview() {
    MemowaveTheme {
        var selected by remember { mutableStateOf(FlashcardGameMode.NUMBERED) }
        FlashcardGameModeSwitcher(
            selectedMode = selected,
            onModeSelected = { selected = it }
        )
    }
}