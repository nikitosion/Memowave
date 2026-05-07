package com.memowave.app.ui.screen.settings.notifications.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.memowave.app.R

/**
 * Скруглённый прямоугольный блок времени для парного выбора (например, "С … До").
 * В отличие от `TimePickerRow`, это самодостаточная карточка без шеврона —
 * визуально подсвечивается как кликабельная.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeBlock(
    label: String,
    hour: Int,
    minute: Int,
    onTimeChange: (hour: Int, minute: Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    var dialogShown by remember { mutableStateOf(false) }

    val container =
        if (enabled) MaterialTheme.colorScheme.surfaceContainerHigh
        else MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.5f)
    val labelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = if (enabled) 0.6f else 0.32f)
    val timeColor = if (enabled) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)

    val timeText = "%02d:%02d".format(hour, minute)
    Column(
        modifier = modifier
            .background(container, RoundedCornerShape(16.dp))
            .clickable(enabled = enabled) { dialogShown = true }
            .padding(horizontal = 14.dp, vertical = 12.dp)
            .semantics(mergeDescendants = true) {
                contentDescription = "$label $timeText"
                role = Role.Button
            },
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = labelColor
        )
        Text(
            text = timeText,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.W600,
            color = timeColor
        )
    }

    if (dialogShown) {
        val state = rememberTimePickerState(
            initialHour = hour,
            initialMinute = minute,
            is24Hour = true
        )
        AlertDialog(
            onDismissRequest = { dialogShown = false },
            title = { Text(label) },
            text = {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    TimePicker(state = state)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    onTimeChange(state.hour, state.minute)
                    dialogShown = false
                }) {
                    Text(stringResource(R.string.action_save))
                }
            },
            dismissButton = {
                TextButton(onClick = { dialogShown = false }) {
                    Text(stringResource(R.string.settings_cancel))
                }
            }
        )
    }
}
