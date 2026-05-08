package com.memowave.app.ui.screen.category_edit

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.memowave.app.ui.common.MemowaveTopBar
import com.memowave.app.ui.common.category.CategoryIcons
import com.memowave.app.ui.common.category.CategoryPalette
import com.memowave.app.ui.screen.library.components.WordCard
import com.memowave.app.ui.theme.MemowaveTheme

private val SuccessGreen = Color(0xFF16A34A)
private val SuccessGreenContainer = Color(0xFFDCFCE7)
private val SuccessGreenContent = Color(0xFF14532D)

@Composable
fun CategoryEditScreen(
    state: CategoryEditUiState,
    onEvent: (CategoryEditEvent) -> Unit,
    onWordClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            CategoryEditTopBar(
                title = stringResource(
                    if (state.isCreate) R.string.category_edit_title_new
                    else R.string.category_edit_title_edit
                ),
                canSave = state.canSave,
                enabled = !state.isBusy,
                onCancel = {
                    if (state.isBusy) return@CategoryEditTopBar
                    onEvent(CategoryEditEvent.CancelRequested)
                },
                onSave = { onEvent(CategoryEditEvent.Save) }
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
                    IdentityCard(state = state, onEvent = onEvent)
                }
                item {
                    ColorPickerSection(state = state, onEvent = onEvent)
                }
                item {
                    IconPickerSection(state = state, onEvent = onEvent)
                }

                if (!state.isCreate && state.words.isNotEmpty()) {
                    item {
                        StatsSection(words = state.words)
                    }
                    item {
                        WordsListHeader(count = state.words.size)
                    }
                    items(
                        items = state.words,
                        key = { it.id }
                    ) { word ->
                        WordCard(
                            word = word,
                            category = state.initialCategory,
                            onClick = { onWordClick(word.id) }
                        )
                    }
                }

                if (!state.isCreate) {
                    item {
                        DeleteButton(
                            enabled = state.canDelete,
                            disabledReason = if (state.words.isNotEmpty())
                                stringResource(R.string.category_edit_delete_disabled_reason)
                            else null,
                            onClick = { onEvent(CategoryEditEvent.DeleteRequested) }
                        )
                    }
                }
            }
        }

        OperationOverlay(operation = state.operation, isCreate = state.isCreate)
    }

    if (state.showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { onEvent(CategoryEditEvent.DeleteCancelled) },
            title = { Text(stringResource(R.string.category_edit_delete_confirm_title)) },
            text = { Text(stringResource(R.string.category_edit_delete_confirm_message)) },
            confirmButton = {
                TextButton(onClick = { onEvent(CategoryEditEvent.DeleteConfirmed) }) {
                    Text(
                        text = stringResource(R.string.category_edit_delete_confirm_yes),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { onEvent(CategoryEditEvent.DeleteCancelled) }) {
                    Text(stringResource(R.string.category_edit_delete_confirm_no))
                }
            }
        )
    }

    if (state.showDiscardConfirm) {
        AlertDialog(
            onDismissRequest = { onEvent(CategoryEditEvent.CancelDismissed) },
            title = { Text(stringResource(R.string.category_edit_discard_title)) },
            text = { Text(stringResource(R.string.category_edit_discard_message)) },
            confirmButton = {
                TextButton(onClick = { onEvent(CategoryEditEvent.CancelConfirmed) }) {
                    Text(
                        text = stringResource(R.string.category_edit_discard_yes),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { onEvent(CategoryEditEvent.CancelDismissed) }) {
                    Text(stringResource(R.string.category_edit_discard_no))
                }
            }
        )
    }
}

// ─── Top bar ──────────────────────────────────────────────────────────────────

@Composable
private fun CategoryEditTopBar(
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
            contentDescription = stringResource(R.string.category_edit_a11y_cancel),
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
            contentDescription = stringResource(R.string.category_edit_a11y_save),
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

// ─── Identity card (icon preview + name + description) ────────────────────────

@Composable
private fun IdentityCard(
    state: CategoryEditUiState,
    onEvent: (CategoryEditEvent) -> Unit
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CategoryPreview(
                color = state.color,
                iconName = state.iconName,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            OutlinedTextField(
                value = state.name,
                onValueChange = { onEvent(CategoryEditEvent.NameChanged(it)) },
                label = { Text(stringResource(R.string.category_edit_field_name)) },
                singleLine = true,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.description,
                onValueChange = { onEvent(CategoryEditEvent.DescriptionChanged(it)) },
                label = { Text(stringResource(R.string.category_edit_field_description)) },
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun CategoryPreview(
    color: String?,
    iconName: String?,
    modifier: Modifier = Modifier
) {
    val parsed = CategoryPalette.parseHexColor(color)
    val container = parsed ?: MaterialTheme.colorScheme.primaryContainer
    val content = if (parsed != null) Color.White else MaterialTheme.colorScheme.onPrimaryContainer
    Box(
        modifier = modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(container),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(CategoryIcons.resolveDrawable(iconName)),
            contentDescription = null,
            tint = content,
            modifier = Modifier.size(36.dp)
        )
    }
}

// ─── Color picker ─────────────────────────────────────────────────────────────

@Composable
private fun ColorPickerSection(
    state: CategoryEditUiState,
    onEvent: (CategoryEditEvent) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionHeader(stringResource(R.string.category_edit_section_color))
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CategoryPalette.SWATCHES.forEach { hex ->
                        ColorSwatch(
                            hex = hex,
                            isSelected = state.color == hex,
                            onClick = { onEvent(CategoryEditEvent.ColorSelected(hex)) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = { onEvent(CategoryEditEvent.ColorSelected(null)) },
                        enabled = state.color != null
                    ) {
                        Text(
                            text = stringResource(R.string.category_edit_color_clear),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                    TextButton(onClick = { onEvent(CategoryEditEvent.ToggleCustomColor) }) {
                        Text(
                            text = stringResource(
                                if (state.isCustomColorOpen) R.string.category_edit_color_hide_custom
                                else R.string.category_edit_color_show_custom
                            ),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }

                if (state.isCustomColorOpen) {
                    OutlinedTextField(
                        value = state.customColorInput,
                        onValueChange = { onEvent(CategoryEditEvent.CustomColorChanged(it)) },
                        placeholder = { Text("#FF000000") },
                        label = { Text(stringResource(R.string.category_edit_color_custom_label)) },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        isError = state.customColorInput.isNotBlank() &&
                            !CategoryPalette.isValidHex(state.customColorInput),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun ColorSwatch(
    hex: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val color = CategoryPalette.parseHexColor(hex) ?: MaterialTheme.colorScheme.surfaceContainer
    Box(
        modifier = modifier
            .height(34.dp)
            .clip(CircleShape)
            .background(color)
            .border(
                width = if (isSelected) 2.5.dp else 0.dp,
                color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                shape = CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                painter = painterResource(R.drawable.round_check_24),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

// ─── Icon picker ──────────────────────────────────────────────────────────────

@Composable
private fun IconPickerSection(
    state: CategoryEditUiState,
    onEvent: (CategoryEditEvent) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionHeader(stringResource(R.string.category_edit_section_icon))
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            val icons = CategoryIcons.ALL
            val rows = icons.chunked(6)
            Column(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                rows.forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        row.forEach { icon ->
                            IconSwatch(
                                drawableRes = icon.drawableRes,
                                isSelected = state.iconName == icon.name ||
                                    (state.iconName == null && icon.name == CategoryIcons.DEFAULT),
                                tint = CategoryPalette.parseHexColor(state.color)
                                    ?: MaterialTheme.colorScheme.primary,
                                onClick = { onEvent(CategoryEditEvent.IconSelected(icon.name)) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        // Pad incomplete rows so cells don't stretch.
                        repeat(6 - row.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun IconSwatch(
    drawableRes: Int,
    isSelected: Boolean,
    tint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val container = if (isSelected) {
        tint.copy(alpha = 0.18f)
    } else {
        MaterialTheme.colorScheme.surfaceContainerHigh
    }
    val border = if (isSelected) tint else Color.Transparent
    val resolvedTint = if (isSelected) tint else MaterialTheme.colorScheme.onSurfaceVariant
    Box(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(container)
            .border(
                width = if (isSelected) 1.5.dp else 0.dp,
                color = border,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(drawableRes),
            contentDescription = null,
            tint = resolvedTint,
            modifier = Modifier.size(22.dp)
        )
    }
}

// ─── Stats section ────────────────────────────────────────────────────────────

@Composable
private fun StatsSection(words: List<Word>) {
    val now = java.time.LocalDateTime.now()
    val total = words.size
    val mastered = words.count { it.phase == 2 }
    val due = words.count { it.phase != 0 && it.dueDate <= now }
    val newCount = words.count { it.phase == 0 }
    val learningCount = words.count { it.phase == 1 }
    val reviewCount = words.count { it.phase == 2 }
    val relearnCount = words.count { it.phase == 3 }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionHeader(stringResource(R.string.category_edit_section_stats))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatTile(
                label = stringResource(R.string.category_edit_stat_total),
                value = total.toString(),
                accent = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            StatTile(
                label = stringResource(R.string.category_edit_stat_mastered),
                value = mastered.toString(),
                accent = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
            StatTile(
                label = stringResource(R.string.category_edit_stat_due),
                value = due.toString(),
                accent = if (due > 0) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )
        }

        PhaseDistribution(
            newCount = newCount,
            learningCount = learningCount,
            reviewCount = reviewCount,
            relearnCount = relearnCount
        )
    }
}

@Composable
private fun StatTile(
    label: String,
    value: String,
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
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.W600,
                color = accent,
                modifier = Modifier.padding(top = 6.dp)
            )
        }
    }
}

@Composable
private fun PhaseDistribution(
    newCount: Int,
    learningCount: Int,
    reviewCount: Int,
    relearnCount: Int
) {
    val total = newCount + learningCount + reviewCount + relearnCount
    if (total == 0) return

    val newColor = MaterialTheme.colorScheme.outline
    val learningColor = MaterialTheme.colorScheme.tertiary
    val reviewColor = MaterialTheme.colorScheme.primary
    val relearnColor = MaterialTheme.colorScheme.error

    val segments = listOf(
        Triple(newCount, newColor, stringResource(R.string.category_edit_phase_new)),
        Triple(learningCount, learningColor, stringResource(R.string.category_edit_phase_learning)),
        Triple(reviewCount, reviewColor, stringResource(R.string.category_edit_phase_review)),
        Triple(relearnCount, relearnColor, stringResource(R.string.category_edit_phase_relearn))
    ).filter { it.first > 0 }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
        ) {
            segments.forEach { (count, color, _) ->
                Box(
                    modifier = Modifier
                        .weight(count.toFloat())
                        .fillMaxSize()
                        .background(color)
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            segments.forEach { (count, color, label) ->
                LegendDot(color = color, label = label, count = count)
            }
        }
    }
}

@Composable
private fun LegendDot(color: Color, label: String, count: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(Modifier.width(5.dp))
        Text(
            text = "$label · $count",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ─── Words list section ───────────────────────────────────────────────────────

@Composable
private fun WordsListHeader(count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.category_edit_section_words),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.W600
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = "($count)",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ─── Section header ───────────────────────────────────────────────────────────

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

// ─── Delete button ────────────────────────────────────────────────────────────

@Composable
private fun DeleteButton(
    enabled: Boolean,
    disabledReason: String?,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
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
                text = stringResource(R.string.category_edit_delete),
                fontWeight = FontWeight.W600
            )
        }
        if (!enabled && disabledReason != null) {
            Text(
                text = disabledReason,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
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
            OperationKind.Save -> stringResource(R.string.category_edit_op_saving)
            OperationKind.Delete -> stringResource(R.string.category_edit_op_deleting)
        }
        OperationPhase.Success -> when (operation.kind) {
            OperationKind.Save -> stringResource(
                if (isCreate) R.string.category_edit_op_save_success_new
                else R.string.category_edit_op_save_success_edit
            )
            OperationKind.Delete -> stringResource(R.string.category_edit_op_delete_success)
        }
        OperationPhase.Error -> {
            val fallback = stringResource(
                when (operation.kind) {
                    OperationKind.Save -> R.string.category_edit_op_save_error
                    OperationKind.Delete -> R.string.category_edit_op_delete_error
                }
            )
            operation.errorMessage?.takeIf { it.isNotBlank() } ?: fallback
        }
    }

// ─── Previews ─────────────────────────────────────────────────────────────────

@Preview(device = "spec:width=411dp,height=891dp", showSystemUi = false, showBackground = true)
@Composable
private fun CategoryEditScreenPreviewEdit() {
    MemowaveTheme {
        CategoryEditScreen(
            state = CategoryEditUiState(
                isCreate = false,
                name = "Природа",
                description = "Природа и стихии",
                color = "#FF22C55E",
                iconName = "globe",
                initialCategory = Category(id = 1, name = "Природа"),
                words = listOf(
                    Word(id = 1, original = "ocean", translation = "океан", phase = 2, reviewCount = 8),
                    Word(id = 2, original = "mountain", translation = "гора", phase = 1, reviewCount = 3),
                    Word(id = 3, original = "river", translation = "река", phase = 0, reviewCount = 0),
                )
            ),
            onEvent = {},
            onWordClick = {}
        )
    }
}

@Preview(device = "spec:width=411dp,height=891dp", showSystemUi = false, showBackground = true)
@Composable
private fun CategoryEditScreenPreviewCreate() {
    MemowaveTheme {
        CategoryEditScreen(
            state = CategoryEditUiState(isCreate = true),
            onEvent = {},
            onWordClick = {}
        )
    }
}
