package com.memowave.app.ui.common.settings

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.memowave.app.R

/**
 * Универсальная строка настроек: иконка-чип + title + supporting + trailing slot.
 *
 * По умолчанию справа отображается chevron, что подходит для навигации.
 * Передайте свой [trailing] для отображения переключателя/значения.
 */
@Composable
fun SettingsRow(
    title: String,
    modifier: Modifier = Modifier,
    @DrawableRes leadingIconRes: Int? = null,
    accent: AccentTone = AccentTone.NEUTRAL,
    supportingText: String? = null,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
    trailing: @Composable RowScope.() -> Unit = { ChevronTrailing() }
) {
    val tint = if (enabled) MaterialTheme.colorScheme.onSurface
    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .let { if (onClick != null && enabled) it.clickable(onClick = onClick) else it }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (leadingIconRes != null) {
            AccentIconChip(iconRes = leadingIconRes, accent = accent)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.W500,
                color = tint
            )
            if (supportingText != null) {
                Text(
                    text = supportingText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
        trailing()
    }
}

@Composable
fun AccentIconChip(
    @DrawableRes iconRes: Int,
    accent: AccentTone,
    modifier: Modifier = Modifier
) {
    val colors = accent.colors()
    Box(
        modifier = modifier
            .size(36.dp)
            .background(
                color = colors.container,
                shape = RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier.size(20.dp),
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = colors.onContainer
        )
    }
}

@Composable
fun ChevronTrailing(modifier: Modifier = Modifier) {
    Icon(
        modifier = modifier.size(20.dp),
        painter = painterResource(R.drawable.round_chevron_right_24),
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
    )
}
