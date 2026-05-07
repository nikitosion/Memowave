package com.memowave.app.ui.common.settings

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Строка с текущим значением справа (например, "Светлая", "9:30").
 * Тап открывает экран/диалог.
 */
@Composable
fun SettingsValueRow(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    @DrawableRes leadingIconRes: Int? = null,
    accent: AccentTone = AccentTone.NEUTRAL,
    enabled: Boolean = true,
    showChevron: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    SettingsRow(
        modifier = modifier,
        title = title,
        leadingIconRes = leadingIconRes,
        accent = accent,
        enabled = enabled,
        onClick = onClick,
        trailing = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    modifier = Modifier.padding(end = if (showChevron) 6.dp else 0.dp),
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                if (showChevron) ChevronTrailing()
            }
        }
    )
}
