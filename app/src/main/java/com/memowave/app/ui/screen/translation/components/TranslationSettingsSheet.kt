package com.memowave.app.ui.screen.translation.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.memowave.app.R
import com.memowave.app.domain.model.Category
import com.memowave.app.ui.common.settings.SectionDivider
import com.memowave.app.ui.common.settings.SettingsItemColumn
import com.memowave.app.ui.common.settings.SettingsSection
import com.memowave.app.ui.common.settings.SettingsToggleRow
import com.memowave.app.ui.common.settings.mutedFilterChipColors
import com.memowave.app.ui.screen.learning_shared.LearningPhase
import com.memowave.app.ui.screen.translation.TypoStrictness

private val WORD_COUNT_OPTIONS = listOf(5, 10, 15, 20, 0) // 0 = All

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranslationSettingsSheet(
    wordCount: Int,
    isShuffled: Boolean,
    typoStrictness: TypoStrictness,
    gamePhase: LearningPhase,
    selectedCategoryId: Long?,
    categories: List<Category>,
    onWordCountChanged: (Int) -> Unit,
    onShuffledChanged: (Boolean) -> Unit,
    onStrictnessChanged: (TypoStrictness) -> Unit,
    onCategorySelected: (Long?) -> Unit,
    onWordCountLocked: () -> Unit,
    onCategoryLocked: () -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
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
                text = stringResource(R.string.settings_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.W700,
                textAlign = TextAlign.Center
            )

            SettingsSection(title = stringResource(R.string.translation_strictness_title)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TypoStrictness.values().forEach { option ->
                        FilterChip(
                            selected = typoStrictness == option,
                            onClick = { onStrictnessChanged(option) },
                            label = {
                                Text(
                                    text = stringResource(
                                        when (option) {
                                            TypoStrictness.STRICT -> R.string.translation_strictness_strict
                                            TypoStrictness.NORMAL -> R.string.translation_strictness_normal
                                            TypoStrictness.LENIENT -> R.string.translation_strictness_lenient
                                        }
                                    ),
                                    style = MaterialTheme.typography.labelLarge
                                )
                            },
                            colors = mutedFilterChipColors()
                        )
                    }
                }
            }

            SettingsSection(title = "Колода") {
                SettingsItemColumn(label = "Количество слов") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (gamePhase == LearningPhase.GAME) {
                            val label = if (wordCount == 0) "Все" else wordCount.toString()
                            FilterChip(
                                selected = true,
                                onClick = onWordCountLocked,
                                label = { Text(text = label, style = MaterialTheme.typography.labelLarge) },
                                colors = mutedFilterChipColors()
                            )
                        } else {
                            WORD_COUNT_OPTIONS.forEach { count ->
                                val label = if (count == 0) "Все" else count.toString()
                                FilterChip(
                                    selected = wordCount == count,
                                    onClick = { onWordCountChanged(count) },
                                    label = { Text(text = label, style = MaterialTheme.typography.labelLarge) },
                                    colors = mutedFilterChipColors()
                                )
                            }
                        }
                    }
                }
                SectionDivider()
                SettingsItemColumn(label = "Набор слов") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (gamePhase == LearningPhase.GAME) {
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
            }
        }
    }
}
