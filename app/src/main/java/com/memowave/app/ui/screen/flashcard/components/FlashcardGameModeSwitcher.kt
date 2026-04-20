package com.memowave.app.ui.screen.flashcard.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.memowave.app.R
import com.memowave.app.ui.screen.flashcard.FlashcardGameMode
import com.memowave.app.ui.theme.MemowaveTheme

@Composable
fun FlashcardGameModeSwitcher(
    modifier: Modifier = Modifier,
    selectedMode: FlashcardGameMode = FlashcardGameMode.BINARY,
    onModeSelected: (FlashcardGameMode) -> Unit = {}
) {
    val colorScheme = MaterialTheme.colorScheme

    val leftSelected = selectedMode == FlashcardGameMode.BINARY
    val rightSelected = selectedMode == FlashcardGameMode.NUMBERED

    val dimFill = colorScheme.onSurface.copy(alpha = 0.04f)
    val dimBorder = colorScheme.onSurface.copy(alpha = 0.06f)
    val dimContent = colorScheme.onSurface.copy(alpha = 0.19f)

    val activeFill = colorScheme.onSecondaryContainer.copy(alpha = 0.08f)
    val activeBorder = colorScheme.onSecondaryContainer.copy(alpha = 0.2f)
    val activeContent = colorScheme.onSecondaryContainer.copy(alpha = 0.8f)

    val leftPanelBg by animateColorAsState(
        if (leftSelected) colorScheme.secondaryContainer else colorScheme.surfaceContainerHigh
    )
    val leftAccent by animateColorAsState(if (leftSelected) activeFill else dimFill)
    val leftOnAccent by animateColorAsState(if (leftSelected) activeContent else dimContent)
    val leftOutlineColor by animateColorAsState(if (leftSelected) activeBorder else dimBorder)
    val leftOutlineContent by animateColorAsState(if (leftSelected) activeContent else dimContent)

    val rightPanelBg by animateColorAsState(
        if (rightSelected) colorScheme.secondaryContainer else colorScheme.surfaceContainerHigh
    )
    val rightChipBg by animateColorAsState(if (rightSelected) activeFill else dimFill)
    val rightChipContent by animateColorAsState(if (rightSelected) activeContent else dimContent)

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
                    .clickable { onModeSelected(FlashcardGameMode.BINARY) }
                    .padding(8.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedIconBox(
                        iconResId = R.drawable.round_close_24,
                        borderColor = leftOutlineColor,
                        contentColor = leftOutlineContent,
                        modifier = Modifier.weight(1f)
                    )
                    FilledIconBox(
                        iconResId = R.drawable.round_check_24,
                        containerColor = leftAccent,
                        contentColor = leftOnAccent,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Text(
                text = "Знаю / Не знаю",
                style = MaterialTheme.typography.labelMedium,
                color = leftOutlineContent
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
private fun OutlinedIconBox(
    iconResId: Int,
    borderColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(56.dp)
            .padding(horizontal = 4.dp)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(30.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(iconResId),
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
private fun FilledIconBox(
    iconResId: Int,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(56.dp)
            .padding(horizontal = 4.dp)
            .background(
                color = containerColor,
                shape = RoundedCornerShape(30.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(iconResId),
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(22.dp)
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
        var selected by remember { mutableStateOf(FlashcardGameMode.BINARY) }
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