package com.memowave.app.ui.screen.settings.personal.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.memowave.app.R
import com.memowave.app.ui.common.settings.AccentTone
import com.memowave.app.ui.common.settings.ChevronTrailing
import com.memowave.app.ui.common.settings.SettingsRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.ui.res.painterResource

/**
 * Нередактируемое поле email — выглядит как обычная settings-строка с
 * info-иконкой справа. Тап показывает диалог-подсказку, что для смены email
 * нужно обратиться в поддержку.
 */
@Composable
fun EmailReadOnlyField(
    email: String,
    modifier: Modifier = Modifier,
    label: String = stringResource(R.string.settings_personal_email),
    tooltip: String = stringResource(R.string.settings_personal_email_tooltip)
) {
    var dialogShown by remember { mutableStateOf(false) }

    SettingsRow(
        modifier = modifier,
        title = label,
        leadingIconRes = R.drawable.round_alternate_email_24,
        accent = AccentTone.SECONDARY,
        supportingText = email.ifBlank { "—" },
        onClick = { dialogShown = true },
        trailing = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    modifier = Modifier
                        .padding(end = 6.dp)
                        .size(20.dp),
                    painter = painterResource(R.drawable.round_help_outline_24),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
                )
                ChevronTrailing()
            }
        }
    )

    if (dialogShown) {
        AlertDialog(
            onDismissRequest = { dialogShown = false },
            title = { Text(label) },
            text = { Text(tooltip) },
            confirmButton = {
                TextButton(onClick = { dialogShown = false }) {
                    Text(stringResource(R.string.action_dismiss))
                }
            }
        )
    }
}
