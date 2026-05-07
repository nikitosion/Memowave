package com.memowave.app.ui.screen.settings.personal.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.memowave.app.R

/**
 * Редактируемое поле username с кнопками Сохранить/Отмена.
 * Кнопки показываются только когда значение отличается от исходного (`originalValue`).
 *
 * Ошибки отрисовываются под полем, ошибочное состояние подсвечивает border.
 */
@Composable
fun UsernameEditField(
    value: String,
    originalValue: String,
    onValueChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    isSubmitting: Boolean = false,
    error: String? = null
) {
    val dirty = value.trim() != originalValue.trim()
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            enabled = !isSubmitting,
            isError = error != null,
            label = { Text(stringResource(R.string.settings_personal_username)) },
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.round_person_24),
                    contentDescription = null
                )
            },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                imeAction = ImeAction.Done
            ),
            shape = RoundedCornerShape(20.dp),
            supportingText = if (error != null) {
                { Text(text = error, color = MaterialTheme.colorScheme.error) }
            } else null
        )
        if (dirty) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onCancel, enabled = !isSubmitting) {
                    Text(stringResource(R.string.settings_cancel))
                }
                TextButton(
                    onClick = onSave,
                    enabled = !isSubmitting,
                    modifier = Modifier.padding(start = 4.dp)
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(stringResource(R.string.settings_personal_username_save))
                    }
                }
            }
        }
    }
}
