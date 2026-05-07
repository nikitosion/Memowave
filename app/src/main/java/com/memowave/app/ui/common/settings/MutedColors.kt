package com.memowave.app.ui.common.settings

import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable

@Composable
fun mutedFilterChipColors() = FilterChipDefaults.filterChipColors(
    selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
    selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
    disabledSelectedContainerColor = MaterialTheme.colorScheme.secondaryContainer
)

@Composable
fun mutedSwitchColors() = SwitchDefaults.colors(
    checkedThumbColor = MaterialTheme.colorScheme.onSecondaryContainer,
    checkedTrackColor = MaterialTheme.colorScheme.secondaryContainer,
    checkedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.4f),
    uncheckedThumbColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
    uncheckedBorderColor = MaterialTheme.colorScheme.outlineVariant
)
