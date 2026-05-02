package com.memowave.app.ui.screen.flashcard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

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
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
            .padding(horizontal = 16.dp)
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            text = "Настройки",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.W700,
            textAlign = TextAlign.Center
        )

        SettingsSection(title = "Режим") {
            FlashcardGameModeSwitcher(
                onModeSelected = onGameModeChanged,
                selectedMode = if (gameMode == FlashcardGameMode.NUMBERED) FlashcardGameMode.NUMBERED else FlashcardGameMode.RECALL,
            )
        }

        SettingsSection(title = "Колода") {
            // Word count
            SettingsItemColumn(label = "Количество слов") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
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

            SectionDivider()

            // Category selector
            SettingsItemColumn(label = "Набор слов") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
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
        }

        SettingsSection(title = "Дополнительно") {
            SettingsToggleRow(
                label = "Перемешивать",
                checked = isShuffled,
                onCheckedChange = onShuffledChanged
            )
            SectionDivider()
            SettingsToggleRow(
                label = "Показывать перевод первым",
                checked = showTranslationFirst,
                onCheckedChange = onShowTranslationFirstChanged
            )
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            modifier = Modifier.padding(start = 16.dp),
            text = title.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.2.sp),
            fontWeight = FontWeight.W700,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun SettingsItemColumn(
    label: String,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.W500,
            color = MaterialTheme.colorScheme.onSurface
        )
        content()
    }
}

@Composable
private fun SectionDivider() {
    HorizontalDivider(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    )
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
            modifier = Modifier.padding(end = 12.dp),
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.W500,
            color = MaterialTheme.colorScheme.onSurface
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
            gameMode = FlashcardGameMode.RECALL,
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
