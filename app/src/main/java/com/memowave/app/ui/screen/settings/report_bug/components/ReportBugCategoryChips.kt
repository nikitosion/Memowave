package com.memowave.app.ui.screen.settings.report_bug.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.memowave.app.ui.common.settings.mutedFilterChipColors
import com.memowave.app.ui.screen.settings.report_bug.BugCategory

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ReportBugCategoryChips(
    selected: BugCategory,
    onSelect: (BugCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        BugCategory.values().forEach { category ->
            FilterChip(
                selected = selected == category,
                onClick = { onSelect(category) },
                label = { Text(stringResource(category.labelRes)) },
                colors = mutedFilterChipColors()
            )
        }
    }
}
