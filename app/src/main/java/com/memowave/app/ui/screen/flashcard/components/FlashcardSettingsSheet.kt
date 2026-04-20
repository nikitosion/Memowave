package com.memowave.app.ui.screen.flashcard.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.memowave.app.domain.model.Category
import com.memowave.app.ui.common.notification.NotificationHost
import com.memowave.app.ui.common.notification.NotificationManager
import com.memowave.app.ui.screen.flashcard.FlashcardGameMode
import com.memowave.app.ui.screen.flashcard.FlashcardPhase
import com.memowave.app.ui.theme.MemowaveTheme

private val WORD_COUNT_OPTIONS = listOf(5, 10, 15, 20, 25, 0) // 0 = All

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardSettingsSheet(
    wordCount: Int,
    isShuffled: Boolean,
    gamePhase: FlashcardPhase,
    gameMode: FlashcardGameMode,
    showTranslationFirst: Boolean,
    selectedCategoryId: Long?,
    categories: List<Category>,
    onWordCountChanged: (Int) -> Unit,
    onShuffledChanged: (Boolean) -> Unit,
    onGameModeChanged: (FlashcardGameMode) -> Unit,
    onShowTranslationFirstChanged: (Boolean) -> Unit,
    onCategorySelected: (Long?) -> Unit,
    onWordCountLocked: () -> Unit,
    onCategoryLocked: () -> Unit,
    onDismiss: () -> Unit,
    notificationManager: NotificationManager? = null
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            FlashcardSettingsSheetContent(
                wordCount = wordCount,
                isShuffled = isShuffled,
                gamePhase = gamePhase,
                gameMode = gameMode,
                showTranslationFirst = showTranslationFirst,
                selectedCategoryId = selectedCategoryId,
                categories = categories,
                onWordCountChanged = onWordCountChanged,
                onShuffledChanged = onShuffledChanged,
                onGameModeChanged = onGameModeChanged,
                onShowTranslationFirstChanged = onShowTranslationFirstChanged,
                onCategorySelected = onCategorySelected,
                onWordCountLocked = onWordCountLocked,
                onCategoryLocked = onCategoryLocked
            )
            if (notificationManager != null) {
                NotificationHost(
                    manager = notificationManager,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                        .padding(bottom = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun FlashcardSettingsSheetContent(
    wordCount: Int,
    isShuffled: Boolean,
    gamePhase: FlashcardPhase,
    gameMode: FlashcardGameMode,
    showTranslationFirst: Boolean,
    selectedCategoryId: Long?,
    categories: List<Category>,
    onWordCountChanged: (Int) -> Unit,
    onShuffledChanged: (Boolean) -> Unit,
    onGameModeChanged: (FlashcardGameMode) -> Unit,
    onShowTranslationFirstChanged: (Boolean) -> Unit,
    onCategorySelected: (Long?) -> Unit,
    onWordCountLocked: () -> Unit = {},
    onCategoryLocked: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "Настройки",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.W600
        )

        Column() {
            Text(
                text = "Выбор ответов",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.W500,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            FlashcardGameModeSwitcher(
                modifier = Modifier.padding(top = 8.dp),
                onModeSelected = onGameModeChanged,
                selectedMode = if (gameMode == FlashcardGameMode.NUMBERED) FlashcardGameMode.NUMBERED else FlashcardGameMode.BINARY,
            )
        }

        // Word count
        Column {
            Text(
                text = "Количество слов",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.W500,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (gamePhase == FlashcardPhase.GAME) {
                    val label = if (wordCount == 0) "Все" else wordCount.toString()
                    FilterChip(
                        selected = true,
                        onClick = onWordCountLocked,
                        label = {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelLarge
                            )
                        },
                        colors = mutedFilterChipColors()
                    )
                } else {
                    WORD_COUNT_OPTIONS.forEach { count ->
                        val label = if (count == 0) "Все" else count.toString()
                        FilterChip(
                            selected = wordCount == count,
                            onClick = { onWordCountChanged(count) },
                            label = {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            },
                            colors = mutedFilterChipColors()
                        )
                    }
                }
            }
        }

        // Category selector
        Column {
            Text(
                text = "Набор слов",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.W500,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (gamePhase == FlashcardPhase.GAME) {
                    val selectedCategory = categories.find { it.id == selectedCategoryId }
                    FilterChip(
                        selected = true,
                        onClick = onCategoryLocked,
                        label = { Text(selectedCategory?.name ?: "Все слова") },
                        colors = mutedFilterChipColors()
                    )
                } else {
                    FilterChip(
                        selected = selectedCategoryId == null,
                        onClick = { onCategorySelected(null) },
                        label = { Text("Все слова") },
                        colors = mutedFilterChipColors()
                    )
                    categories.forEach { category ->
                        FilterChip(
                            selected = selectedCategoryId == category.id,
                            onClick = { onCategorySelected(category.id) },
                            label = { Text(category.name) },
                            colors = mutedFilterChipColors()
                        )
                    }
                }
            }
        }

        // Shuffle toggle
        SettingsToggleRow(
            label = "Перемешивать",
            checked = isShuffled,
            onCheckedChange = onShuffledChanged
        )

        // Translation first toggle
        SettingsToggleRow(
            label = "Показывать перевод первым",
            checked = showTranslationFirst,
            onCheckedChange = onShowTranslationFirstChanged
        )
    }
}

@Composable
private fun SettingsToggleRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.W500,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = mutedSwitchColors()
        )
    }
}

@Composable
private fun mutedFilterChipColors() = FilterChipDefaults.filterChipColors(
    selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
    selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
    disabledSelectedContainerColor = MaterialTheme.colorScheme.secondaryContainer
)

@Composable
private fun mutedSwitchColors() = SwitchDefaults.colors(
    checkedThumbColor = MaterialTheme.colorScheme.onSecondaryContainer,
    checkedTrackColor = MaterialTheme.colorScheme.secondaryContainer,
    checkedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.4f),
    uncheckedThumbColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
    uncheckedBorderColor = MaterialTheme.colorScheme.outlineVariant
)

@Preview(showBackground = true)
@Composable
private fun FlashcardSettingsSheetContentPreview() {
    MemowaveTheme {
        FlashcardSettingsSheetContent(
            wordCount = 10,
            isShuffled = true,
            gameMode = FlashcardGameMode.BINARY,
            gamePhase = FlashcardPhase.GAME,
            showTranslationFirst = false,
            selectedCategoryId = 1L,
            categories = listOf(
                Category(id = 1L, name = "Базовые"),
                Category(id = 2L, name = "Еда"),
                Category(id = 3L, name = "Путешествия"),
                Category(id = 4L, name = "Работа")
            ),
            onWordCountChanged = {},
            onShuffledChanged = {},
            onShowTranslationFirstChanged = {},
            onGameModeChanged = {},
            onCategorySelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FlashcardSettingsSheetContentAllWordsPreview() {
    MemowaveTheme {
        FlashcardSettingsSheetContent(
            wordCount = 0,
            isShuffled = false,
            gameMode = FlashcardGameMode.NUMBERED,
            gamePhase = FlashcardPhase.LOBBY,
            showTranslationFirst = true,
            selectedCategoryId = null,
            categories = listOf(
                Category(id = 1L, name = "Базовые"),
                Category(id = 2L, name = "Еда"),
                Category(id = 3L, name = "Путешествия"),
                Category(id = 4L, name = "Работа")
            ),
            onWordCountChanged = {},
            onShuffledChanged = {},
            onShowTranslationFirstChanged = {},
            onGameModeChanged = {},
            onCategorySelected = {}
        )
    }
}
