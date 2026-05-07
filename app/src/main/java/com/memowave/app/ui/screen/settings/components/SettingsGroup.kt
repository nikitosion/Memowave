package com.memowave.app.ui.screen.settings.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.memowave.app.ui.common.settings.AccentTone
import com.memowave.app.ui.common.settings.SectionDivider
import com.memowave.app.ui.common.settings.SettingsRow

data class SettingsNavItem(
    val title: String,
    @DrawableRes val iconRes: Int,
    val accent: AccentTone = AccentTone.NEUTRAL,
    val supportingText: String? = null,
    val onClick: () -> Unit
)

@Composable
fun SettingsGroup(
    items: List<SettingsNavItem>,
    modifier: Modifier = Modifier,
    title: String? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (title != null) {
            Text(
                modifier = Modifier.padding(start = 16.dp, bottom = 8.dp),
                text = title.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.2.sp),
                fontWeight = FontWeight.W700,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            items.forEachIndexed { index, item ->
                SettingsRow(
                    title = item.title,
                    leadingIconRes = item.iconRes,
                    accent = item.accent,
                    supportingText = item.supportingText,
                    onClick = item.onClick
                )
                if (index < items.lastIndex) {
                    SectionDivider(modifier = Modifier.padding(start = 60.dp))
                }
            }
        }
    }
}
