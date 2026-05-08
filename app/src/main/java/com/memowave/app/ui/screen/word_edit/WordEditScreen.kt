package com.memowave.app.ui.screen.word_edit

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.memowave.app.R
import com.memowave.app.domain.model.Category
import com.memowave.app.domain.model.Word
import com.memowave.app.ui.common.media.ImagePickerField
import com.memowave.app.ui.theme.MemowaveTheme
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

private val SuccessGreen = Color(0xFF16A34A)
private val SuccessGreenContainer = Color(0xFFDCFCE7)
private val SuccessGreenContent = Color(0xFF14532D)

@Composable
fun WordEditScreen(
    state: WordEditUiState,
    onEvent: (WordEditEvent) -> Unit,
    onUploadImage: suspend (Uri) -> Result<String>,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            WordEditTopBar(
                title = stringResource(
                    if (state.isCreate) R.string.word_edit_title_new
                    else R.string.word_edit_title_edit
                ),
                canSave = state.canSave,
                enabled = !state.isBusy,
                onCancel = {
                    if (state.isBusy) return@WordEditTopBar
                    onEvent(WordEditEvent.CancelRequested)
                },
                onSave = { onEvent(WordEditEvent.Save) }
            )

            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 48.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    CircularProgressIndicator()
                }
                return@Column
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 8.dp,
                    bottom = 32.dp
                )
            ) {
                item {
                    ImagePickerField(
                        currentFileName = state.imageFileName,
                        onUpload = onUploadImage,
                        onFileNameChange = { onEvent(WordEditEvent.ImageChanged(it)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    MainFieldsCard(state = state, onEvent = onEvent)
                }

                if (!state.isCreate && state.initialWord != null) {
                    item {
                        AlgorithmSection(
                            word = state.initialWord,
                            progressResetRequested = state.progressResetRequested,
                            onResetClick = { onEvent(WordEditEvent.ResetProgressRequested) },
                            onUndoResetClick = { onEvent(WordEditEvent.UndoResetProgress) }
                        )
                    }
                    item {
                        MetadataRow(word = state.initialWord)
                    }
                    item {
                        DeleteButton(
                            enabled = !state.isBusy,
                            onClick = { onEvent(WordEditEvent.DeleteRequested) }
                        )
                    }
                }
            }
        }

        OperationOverlay(operation = state.operation, isCreate = state.isCreate)
    }

    if (state.showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { onEvent(WordEditEvent.DeleteCancelled) },
            title = { Text(stringResource(R.string.word_edit_delete_confirm_title)) },
            text = { Text(stringResource(R.string.word_edit_delete_confirm_message)) },
            confirmButton = {
                TextButton(onClick = { onEvent(WordEditEvent.DeleteConfirmed) }) {
                    Text(
                        text = stringResource(R.string.word_edit_delete_confirm_yes),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { onEvent(WordEditEvent.DeleteCancelled) }) {
                    Text(stringResource(R.string.word_edit_delete_confirm_no))
                }
            }
        )
    }

    if (state.showDiscardConfirm) {
        AlertDialog(
            onDismissRequest = { onEvent(WordEditEvent.CancelDismissed) },
            title = { Text(stringResource(R.string.word_edit_discard_title)) },
            text = { Text(stringResource(R.string.word_edit_discard_message)) },
            confirmButton = {
                TextButton(onClick = { onEvent(WordEditEvent.CancelConfirmed) }) {
                    Text(
                        text = stringResource(R.string.word_edit_discard_yes),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { onEvent(WordEditEvent.CancelDismissed) }) {
                    Text(stringResource(R.string.word_edit_discard_no))
                }
            }
        )
    }

}

// ─── Top bar ──────────────────────────────────────────────────────────────────

@Composable
private fun WordEditTopBar(
    title: String,
    canSave: Boolean,
    enabled: Boolean,
    onCancel: () -> Unit,
    onSave: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedIconAction(
            icon = R.drawable.round_close_24,
            contentDescription = stringResource(R.string.word_edit_a11y_cancel),
            tint = MaterialTheme.colorScheme.onSurface,
            enabled = enabled,
            onClick = onCancel
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.W600
            )
        }
        OutlinedIconAction(
            icon = R.drawable.round_check_24,
            contentDescription = stringResource(R.string.word_edit_a11y_save),
            tint = MaterialTheme.colorScheme.primary,
            enabled = canSave,
            onClick = onSave
        )
    }
}

@Composable
private fun OutlinedIconAction(
    icon: Int,
    contentDescription: String,
    tint: Color,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (enabled) {
        MaterialTheme.colorScheme.outline
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
    }
    val resolvedTint = if (enabled) tint else tint.copy(alpha = 0.4f)
    val shape = RoundedCornerShape(14.dp)
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(shape)
            .border(width = 1.5.dp, color = borderColor, shape = shape)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = contentDescription,
            tint = resolvedTint,
            modifier = Modifier.size(22.dp)
        )
    }
}

// ─── Main fields card ─────────────────────────────────────────────────────────

@Composable
private fun MainFieldsCard(
    state: WordEditUiState,
    onEvent: (WordEditEvent) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = state.original,
                onValueChange = { onEvent(WordEditEvent.OriginalChanged(it)) },
                label = { Text(stringResource(R.string.word_edit_field_original)) },
                singleLine = true,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.translation,
                onValueChange = { onEvent(WordEditEvent.TranslationChanged(it)) },
                label = { Text(stringResource(R.string.word_edit_field_translation)) },
                singleLine = true,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.example,
                onValueChange = { onEvent(WordEditEvent.ExampleChanged(it)) },
                label = { Text(stringResource(R.string.word_edit_field_example)) },
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = stringResource(R.string.word_edit_field_category),
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )
            CategoryRow(
                categories = state.categories,
                selectedId = state.selectedCategoryId,
                onSelected = { onEvent(WordEditEvent.CategorySelected(it)) }
            )
        }
    }
}

@Composable
private fun CategoryRow(
    categories: List<Category>,
    selectedId: Long?,
    onSelected: (Long?) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        CategoryRowItem(
            label = stringResource(R.string.word_edit_category_none),
            isSelected = selectedId == null,
            onClick = { onSelected(null) }
        )
        categories.forEach { category ->
            CategoryRowItem(
                label = category.name,
                isSelected = selectedId == category.id,
                onClick = { onSelected(category.id) }
            )
        }
    }
}

@Composable
private fun CategoryRowItem(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(
                    if (isSelected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surfaceContainerHighest
                )
        )
        Text(
            text = label,
            modifier = Modifier.padding(start = 12.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

// ─── Algorithm section ────────────────────────────────────────────────────────

@Composable
private fun AlgorithmSection(
    word: Word,
    progressResetRequested: Boolean,
    onResetClick: () -> Unit,
    onUndoResetClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionHeader(stringResource(R.string.word_edit_algorithm_title))

        if (progressResetRequested) {
            ResetBanner(onUndoClick = onUndoResetClick)
        }

        HeroPhaseDueCard(word = word)

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatTile(
                icon = R.drawable.round_area_chart_24,
                label = stringResource(R.string.word_edit_algo_review_count),
                value = word.reviewCount.toString(),
                modifier = Modifier.weight(1f)
            )
            StatTile(
                icon = R.drawable.round_arrow_right_alt_24,
                label = stringResource(R.string.word_edit_algo_interval),
                value = stringResource(R.string.word_edit_algo_days_format, word.interval),
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatTileWithProgress(
                icon = R.drawable.round_fire_24,
                label = stringResource(R.string.word_edit_algo_stability),
                value = stringResource(
                    R.string.word_edit_algo_stability_format,
                    word.stability
                ),
                progress = (word.stability / STABILITY_MAX_DAYS).coerceIn(0.0, 1.0).toFloat(),
                accent = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
            StatTileWithProgress(
                icon = R.drawable.round_stars_24,
                label = stringResource(R.string.word_edit_algo_difficulty),
                value = stringResource(
                    R.string.word_edit_algo_difficulty_format,
                    word.difficulty
                ),
                progress = (word.difficulty / 10.0).coerceIn(0.0, 1.0).toFloat(),
                accent = difficultyAccent(word.difficulty),
                modifier = Modifier.weight(1f)
            )
        }

        LastReviewRow(lastReview = word.lastReview)

        if (!progressResetRequested) {
            OutlinedButton(
                onClick = onResetClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
            ) {
                Text(stringResource(R.string.word_edit_algo_reset))
            }
        }
    }
}

private const val STABILITY_MAX_DAYS = 60.0

@Composable
private fun difficultyAccent(value: Double): Color {
    val normalized = (value / 10.0).coerceIn(0.0, 1.0)
    return when {
        normalized < 0.34 -> SuccessGreen
        normalized < 0.67 -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.error
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.W600,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
    )
}

@Composable
private fun ResetBanner(onUndoClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.round_warning_24),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.word_edit_algo_reset_banner),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.weight(1f)
            )
            TextButton(
                onClick = onUndoClick,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Text(
                    text = stringResource(R.string.word_edit_algo_reset_undo),
                    fontWeight = FontWeight.W600
                )
            }
        }
    }
}

@Composable
private fun HeroPhaseDueCard(word: Word) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.word_edit_algo_phase_caption),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))
                PhaseChip(phase = word.phase)
            }
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(R.string.word_edit_algo_due_caption),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))
                if (word.phase == 0) {
                    Text(
                        text = stringResource(R.string.word_edit_algo_due_ready),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.W600,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Text(
                        text = formatDueRelative(word.dueDate),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.W600,
                        color = dueAccent(word.dueDate)
                    )
                    Text(
                        text = formatDate(word.dueDate),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

private data class PhaseStyle(
    val label: String,
    val container: Color,
    val content: Color
)

@Composable
private fun phaseStyle(phase: Int): PhaseStyle = when (phase) {
    0 -> PhaseStyle(
        label = stringResource(R.string.word_edit_algo_phase_new),
        container = MaterialTheme.colorScheme.primaryContainer,
        content = MaterialTheme.colorScheme.onPrimaryContainer
    )
    1 -> PhaseStyle(
        label = stringResource(R.string.word_edit_algo_phase_learning),
        container = MaterialTheme.colorScheme.tertiaryContainer,
        content = MaterialTheme.colorScheme.onTertiaryContainer
    )
    2 -> PhaseStyle(
        label = stringResource(R.string.word_edit_algo_phase_review),
        container = MaterialTheme.colorScheme.secondaryContainer,
        content = MaterialTheme.colorScheme.onSecondaryContainer
    )
    else -> PhaseStyle(
        label = stringResource(R.string.word_edit_algo_phase_relearn),
        container = MaterialTheme.colorScheme.errorContainer,
        content = MaterialTheme.colorScheme.onErrorContainer
    )
}

@Composable
private fun PhaseChip(phase: Int) {
    val style = phaseStyle(phase)
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(style.container)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = style.label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.W600,
            color = style.content
        )
    }
}

@Composable
private fun StatTile(
    icon: Int,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.W600,
                modifier = Modifier.padding(top = 6.dp)
            )
        }
    }
}

@Composable
private fun StatTileWithProgress(
    icon: Int,
    label: String,
    value: String,
    progress: Float,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.W600,
                modifier = Modifier.padding(top = 6.dp)
            )
            LinearProgressIndicator(
                progress = { progress },
                color = accent,
                trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp))
            )
        }
    }
}

@Composable
private fun LastReviewRow(lastReview: LocalDateTime?) {
    val text = if (lastReview == null) {
        stringResource(R.string.word_edit_algo_last_review_never)
    } else {
        stringResource(R.string.word_edit_algo_last_review_format, formatDate(lastReview))
    }
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 4.dp)
    )
}

@Composable
private fun MetadataRow(word: Word) {
    Column(
        modifier = Modifier.padding(horizontal = 4.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = stringResource(
                R.string.word_edit_meta_created,
                formatTimestamp(word.createdAt)
            ),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline
        )
        Text(
            text = stringResource(
                R.string.word_edit_meta_updated,
                formatTimestamp(word.updatedAt)
            ),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
private fun DeleteButton(
    enabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer,
            disabledContainerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f),
            disabledContentColor = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.5f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.round_delete_24),
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = stringResource(R.string.word_edit_delete),
            fontWeight = FontWeight.W600
        )
    }
}

// ─── Operation overlay ────────────────────────────────────────────────────────

@Composable
private fun OperationOverlay(
    operation: OperationStatus?,
    isCreate: Boolean
) {
    AnimatedVisibility(
        visible = operation != null,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        if (operation == null) return@AnimatedVisibility
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.35f))
                .clickable(enabled = false, onClick = {}),
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(
                visible = true,
                enter = scaleIn(initialScale = 0.85f) + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                OperationCard(operation = operation, isCreate = isCreate)
            }
        }
    }
}

@Composable
private fun OperationCard(
    operation: OperationStatus,
    isCreate: Boolean
) {
    val (container, content, accent) = when (operation.phase) {
        OperationPhase.InProgress -> Triple(
            MaterialTheme.colorScheme.surface,
            MaterialTheme.colorScheme.onSurface,
            MaterialTheme.colorScheme.primary
        )
        OperationPhase.Success -> Triple(
            SuccessGreenContainer,
            SuccessGreenContent,
            SuccessGreen
        )
        OperationPhase.Error -> Triple(
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer,
            MaterialTheme.colorScheme.error
        )
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = container),
        modifier = Modifier.padding(horizontal = 32.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 28.dp, vertical = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (operation.phase) {
                OperationPhase.InProgress -> CircularProgressIndicator(
                    color = accent,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(36.dp)
                )
                OperationPhase.Success -> Icon(
                    painter = painterResource(R.drawable.round_check_circle_24),
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(40.dp)
                )
                OperationPhase.Error -> Icon(
                    painter = painterResource(R.drawable.round_error_24),
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(40.dp)
                )
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = operationLabel(operation, isCreate),
                color = content,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.W600
            )
        }
    }
}

@Composable
private fun operationLabel(operation: OperationStatus, isCreate: Boolean): String =
    when (operation.phase) {
        OperationPhase.InProgress -> when (operation.kind) {
            OperationKind.Save -> stringResource(R.string.word_edit_op_saving)
            OperationKind.Delete -> stringResource(R.string.word_edit_op_deleting)
        }
        OperationPhase.Success -> when (operation.kind) {
            OperationKind.Save -> stringResource(
                if (isCreate) R.string.word_edit_op_save_success_new
                else R.string.word_edit_op_save_success_edit
            )
            OperationKind.Delete -> stringResource(R.string.word_edit_op_delete_success)
        }
        OperationPhase.Error -> {
            val fallback = stringResource(
                when (operation.kind) {
                    OperationKind.Save -> R.string.word_edit_op_save_error
                    OperationKind.Delete -> R.string.word_edit_op_delete_error
                }
            )
            operation.errorMessage?.takeIf { it.isNotBlank() } ?: fallback
        }
    }

// ─── Date utilities ───────────────────────────────────────────────────────────

private val DATE_FORMATTER: DateTimeFormatter =
    DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)

private fun formatDate(value: LocalDateTime): String =
    runCatching { value.format(DATE_FORMATTER) }.getOrDefault(value.toString())

private fun formatTimestamp(epochMillis: Long): String =
    runCatching {
        val ldt = LocalDateTime.ofInstant(
            java.time.Instant.ofEpochMilli(epochMillis),
            java.time.ZoneId.systemDefault()
        )
        ldt.format(DATE_FORMATTER)
    }.getOrDefault(epochMillis.toString())

@Composable
private fun formatDueRelative(due: LocalDateTime): String {
    val now = LocalDateTime.now()
    val daysBetween = Duration.between(now, due).toDays()
    return when {
        due.isBefore(now.minusMinutes(1)) -> stringResource(R.string.word_edit_algo_due_overdue)
        daysBetween < 1L -> stringResource(R.string.word_edit_algo_due_today)
        daysBetween < 2L -> stringResource(R.string.word_edit_algo_due_tomorrow)
        else -> stringResource(R.string.word_edit_algo_due_in_days_format, daysBetween.toInt())
    }
}

@Composable
private fun dueAccent(due: LocalDateTime): Color {
    val now = LocalDateTime.now()
    return when {
        due.isBefore(now.minusMinutes(1)) -> MaterialTheme.colorScheme.error
        due.isBefore(now.plusDays(1)) -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurface
    }
}

// ─── Previews ─────────────────────────────────────────────────────────────────

@Preview(device = "spec:width=411dp,height=891dp", showSystemUi = false, showBackground = true)
@Composable
private fun WordEditScreenPreviewEdit() {
    MemowaveTheme {
        WordEditScreen(
            state = WordEditUiState(
                isCreate = false,
                original = "ocean",
                translation = "океан",
                example = "The ocean is deep.",
                selectedCategoryId = 1,
                categories = listOf(
                    Category(id = 1, name = "Природа"),
                    Category(id = 2, name = "Путешествия")
                ),
                initialWord = Word(
                    id = 1,
                    original = "ocean",
                    translation = "океан",
                    examples = listOf("The ocean is deep."),
                    reviewCount = 7,
                    interval = 14,
                    phase = 2,
                    stability = 12.34,
                    difficulty = 5.7,
                    lastReview = LocalDateTime.now().minusDays(3),
                    dueDate = LocalDateTime.now().plusDays(11)
                )
            ),
            onEvent = {},
            onUploadImage = { Result.failure(NotImplementedError()) }
        )
    }
}

@Preview(device = "spec:width=411dp,height=891dp", showSystemUi = false, showBackground = true)
@Composable
private fun WordEditScreenPreviewCreate() {
    MemowaveTheme {
        WordEditScreen(
            state = WordEditUiState(
                isCreate = true,
                categories = listOf(Category(id = 1, name = "Базовые"))
            ),
            onEvent = {},
            onUploadImage = { Result.failure(NotImplementedError()) }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun WordEditScreenPreviewSaving() {
    MemowaveTheme {
        WordEditScreen(
            state = WordEditUiState(
                isCreate = false,
                original = "ocean",
                translation = "океан",
                operation = OperationStatus(OperationKind.Save, OperationPhase.InProgress),
                initialWord = Word(id = 1, original = "ocean", translation = "океан")
            ),
            onEvent = {},
            onUploadImage = { Result.failure(NotImplementedError()) }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun WordEditScreenPreviewSuccess() {
    MemowaveTheme {
        WordEditScreen(
            state = WordEditUiState(
                isCreate = false,
                original = "ocean",
                translation = "океан",
                operation = OperationStatus(OperationKind.Save, OperationPhase.Success),
                initialWord = Word(id = 1, original = "ocean", translation = "океан")
            ),
            onEvent = {},
            onUploadImage = { Result.failure(NotImplementedError()) }
        )
    }
}

