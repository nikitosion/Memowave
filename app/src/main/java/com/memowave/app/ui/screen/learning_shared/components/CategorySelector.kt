package com.memowave.app.ui.screen.learning_shared.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.memowave.app.R
import com.memowave.app.domain.model.Category
import com.memowave.app.ui.screen.learning_shared.LoadState
import com.memowave.app.ui.theme.MemowaveTheme

private val EXPANDED_LIST_MAX_HEIGHT = 320.dp

@Composable
fun CategorySelector(
    categories: List<Category>,
    selectedCategoryId: Long?,
    wordCounts: Map<Long, Int>,
    totalWordsCount: Int,
    loadState: LoadState,
    onCategorySelected: (Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (expanded) -90f else 90f,
        animationSpec = tween(durationMillis = 220),
        label = "category_selector_chevron"
    )

    val isLoading = loadState == LoadState.LOADING || loadState == LoadState.IDLE
    val isError = loadState == LoadState.ERROR
    val isLoaded = loadState == LoadState.LOADED

    val selectedName = if (selectedCategoryId == null) {
        stringResource(R.string.flashcard_lobby_category_all)
    } else {
        categories.firstOrNull { it.id == selectedCategoryId }?.name
            ?: stringResource(R.string.flashcard_lobby_category_all)
    }
    val selectedCount = if (selectedCategoryId == null) totalWordsCount
    else wordCounts[selectedCategoryId] ?: 0

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = isLoaded) { expanded = !expanded }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(20.dp),
                    painter = painterResource(R.drawable.round_local_library_24),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.flashcard_lobby_category_label),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = selectedName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.W600,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            TriggerStatusChip(
                isLoading = isLoading,
                isError = isError,
                count = selectedCount
            )
            Icon(
                modifier = Modifier
                    .size(22.dp)
                    .rotate(rotation)
                    .alpha(if (isLoaded) 1f else 0.4f),
                painter = painterResource(R.drawable.round_chevron_right_24),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
            )
        }

        AnimatedVisibility(
            visible = expanded && isLoaded,
            enter = expandVertically(animationSpec = tween(220)) +
                    fadeIn(animationSpec = tween(220)),
            exit = shrinkVertically(animationSpec = tween(180)) +
                    fadeOut(animationSpec = tween(180))
        ) {
            Column {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                Column(
                    modifier = Modifier
                        .heightIn(max = EXPANDED_LIST_MAX_HEIGHT)
                        .verticalScroll(rememberScrollState())
                ) {
                    CategoryRow(
                        name = stringResource(R.string.flashcard_lobby_category_all),
                        description = stringResource(R.string.flashcard_lobby_category_all_description),
                        count = totalWordsCount,
                        isSelected = selectedCategoryId == null,
                        onClick = {
                            onCategorySelected(null)
                            expanded = false
                        }
                    )
                    categories.forEach { category ->
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                        CategoryRow(
                            name = category.name,
                            description = category.description,
                            count = wordCounts[category.id] ?: 0,
                            isSelected = selectedCategoryId == category.id,
                            onClick = {
                                onCategorySelected(category.id)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TriggerStatusChip(
    isLoading: Boolean,
    isError: Boolean,
    count: Int
) {
    when {
        isLoading -> StatusPill(
            text = stringResource(R.string.flashcard_lobby_category_loading),
            container = MaterialTheme.colorScheme.surfaceContainerHighest,
            content = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            withSpinner = true
        )
        isError -> StatusPill(
            text = stringResource(R.string.flashcard_lobby_category_error),
            container = MaterialTheme.colorScheme.errorContainer,
            content = MaterialTheme.colorScheme.onErrorContainer
        )
        count <= 0 -> StatusPill(
            text = stringResource(R.string.flashcard_lobby_category_empty_hint),
            container = MaterialTheme.colorScheme.errorContainer,
            content = MaterialTheme.colorScheme.onErrorContainer
        )
        else -> StatusPill(
            text = pluralStringResource(
                R.plurals.flashcard_lobby_category_words_count, count, count
            ),
            container = MaterialTheme.colorScheme.surfaceContainerHighest,
            content = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
        )
    }
}

@Composable
private fun StatusPill(
    text: String,
    container: androidx.compose.ui.graphics.Color,
    content: androidx.compose.ui.graphics.Color,
    withSpinner: Boolean = false
) {
    Row(
        modifier = Modifier
            .height(26.dp)
            .background(container, RoundedCornerShape(13.dp))
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        if (withSpinner) {
            CircularProgressIndicator(
                modifier = Modifier.size(12.dp),
                strokeWidth = 1.5.dp,
                color = content
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.W600,
            color = content,
            maxLines = 1
        )
    }
}

@Composable
private fun CategoryRow(
    name: String,
    description: String?,
    count: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val isEmpty = count <= 0
    val rowAlpha = if (isEmpty) 0.55f else 1f

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier.size(20.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Icon(
                    modifier = Modifier.size(20.dp),
                    painter = painterResource(R.drawable.round_check_24),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .alpha(rowAlpha)
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = if (isSelected) FontWeight.W600 else FontWeight.W500,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (!description.isNullOrBlank()) {
                Text(
                    modifier = Modifier.padding(top = 2.dp),
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        StatusPill(
            text = if (isEmpty) stringResource(R.string.flashcard_lobby_category_empty_hint)
            else pluralStringResource(R.plurals.flashcard_lobby_category_words_count, count, count),
            container = if (isEmpty) MaterialTheme.colorScheme.errorContainer
            else MaterialTheme.colorScheme.surfaceContainerHighest,
            content = if (isEmpty) MaterialTheme.colorScheme.onErrorContainer
            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CategorySelectorPreview() {
    MemowaveTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            CategorySelector(
                categories = listOf(
                    Category(id = 1L, name = "Базовые", description = "Самые частые слова"),
                    Category(id = 2L, name = "Еда", description = "Продукты и блюда")
                ),
                selectedCategoryId = 1L,
                wordCounts = mapOf(1L to 24, 2L to 12),
                totalWordsCount = 36,
                loadState = LoadState.LOADED,
                onCategorySelected = {}
            )
        }
    }
}
