package com.memowave.app.ui.screen.settings.goals.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.memowave.app.R
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale

/**
 * 7 кружков понедельник–воскресенье. Каждый — toggle.
 * Использует ISO numbering: 1=Mon..7=Sun (как в `AppSettings.goalDaysOfWeek`).
 */
@Composable
fun DayOfWeekChips(
    selected: Set<Int>,
    onToggle: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val days = listOf(
        1 to stringResource(R.string.day_short_mon),
        2 to stringResource(R.string.day_short_tue),
        3 to stringResource(R.string.day_short_wed),
        4 to stringResource(R.string.day_short_thu),
        5 to stringResource(R.string.day_short_fri),
        6 to stringResource(R.string.day_short_sat),
        7 to stringResource(R.string.day_short_sun),
    )
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        days.forEach { (iso, label) ->
            val isSelected = iso in selected
            val container = if (isSelected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.surfaceContainerHigh
            val onContainer = if (isSelected) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
            // TalkBack: full day name + Toggle role + selected state.
            val a11yName = DayOfWeek.of(iso)
                .getDisplayName(TextStyle.FULL, Locale.getDefault())
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(color = container, shape = CircleShape)
                    .clickable { onToggle(iso) }
                    .semantics(mergeDescendants = true) {
                        contentDescription = a11yName
                        role = Role.Switch
                        this.selected = isSelected
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.W600,
                    color = onContainer
                )
            }
        }
    }
}
