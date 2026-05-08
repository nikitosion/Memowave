package com.memowave.app.ui.common.settings

import androidx.annotation.DrawableRes
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

enum class ActionStyle { TEXT, OUTLINED, FILLED }

/**
 * Строка с кнопкой-действием справа. Используется для "Сменить пароль", "Сбросить настройки" и т.п.
 */
@Composable
fun SettingsActionRow(
    title: String,
    actionLabel: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
    @DrawableRes leadingIconRes: Int? = null,
    accent: AccentTone = AccentTone.NEUTRAL,
    enabled: Boolean = true,
    style: ActionStyle = ActionStyle.TEXT,
    supportingText: String? = null
) {
    SettingsRow(
        modifier = modifier,
        title = title,
        leadingIconRes = leadingIconRes,
        accent = accent,
        enabled = enabled,
        supportingText = supportingText,
        onClick = null,
        trailing = {
            when (style) {
                ActionStyle.TEXT -> TextButton(onClick = onAction, enabled = enabled) {
                    Text(actionLabel, style = MaterialTheme.typography.labelLarge)
                }
                ActionStyle.OUTLINED -> OutlinedButton(onClick = onAction, enabled = enabled) {
                    Text(actionLabel, style = MaterialTheme.typography.labelLarge)
                }
                ActionStyle.FILLED -> Button(onClick = onAction, enabled = enabled) {
                    Text(actionLabel, style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    )
}
