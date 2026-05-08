package com.memowave.app.ui.screen.quiz.components

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

private val DURATION_OPTIONS = listOf(30, 60, 120)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizSettingsSheet(
    durationSeconds: Int,
    isShuffled: Boolean,
    gamePhase: LearningPhase,
    selectedCategoryId: Long?,
    categories: List<Category>,
    onDurationChanged: (Int) -> Unit,
    onShuffledChanged: (Boolean) -> Unit,
    onCategorySelected: (Long?) -> Unit,
    onCategoryLocked: () -> Unit,
    onDurationLocked: () -> Unit,
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

            SettingsSection(title = stringResource(R.string.quiz_duration_title)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (gamePhase == LearningPhase.GAME) {
                        FilterChip(
                            selected = true,
                            onClick = onDurationLocked,
                            label = { Text("${durationSeconds}с") },
                            colors = mutedFilterChipColors()
                        )
                    } else {
                        DURATION_OPTIONS.forEach { sec ->
                            FilterChip(
                                selected = durationSeconds == sec,
                                onClick = { onDurationChanged(sec) },
                                label = { Text("${sec}с") },
                                colors = mutedFilterChipColors()
                            )
                        }
                    }
                }
            }

            SettingsSection(title = "Колода") {
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
                SectionDivider()
                SettingsToggleRow(
                    label = "Перемешивать",
                    checked = isShuffled,
                    onCheckedChange = onShuffledChanged
                )
            }
        }
    }
}
