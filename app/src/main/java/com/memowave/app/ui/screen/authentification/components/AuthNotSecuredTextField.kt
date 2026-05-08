package com.memowave.app.ui.screen.authentification.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

/**
 * Text field for non-password authentication input (e.g., email, username).
 *
 * Shows an error message if [error] is not null.
 *
 * @param value Current text value
 * @param onValueChange Callback for text changes
 * @param error Error message to display, or null
 * @param modifier Modifier for styling
 * @param labelText Label for the text field
 * @param placeholderText Placeholder text
 * @param leadingIconResId Resource ID for the leading icon
 * @param imeAction IME action for the keyboard
 */
@Composable
fun AuthNotSecuredTextField(
    value: String,
    labelText: String,
    placeholderText: String,
    @DrawableRes
    leadingIconResId: Int,
    onValueChange: (String) -> Unit,
    error: String? = null,
    modifier: Modifier = Modifier,
    imeAction: ImeAction = ImeAction.Next
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = imeAction
            ),
            label = { Text(text = labelText) },
            placeholder = { Text(text = placeholderText) },
            leadingIcon = {
                Icon(
                    painter = painterResource(
                        id = leadingIconResId
                    ),
                    contentDescription = "Email Icon"
                )
            },
            isError = error != null,
            shape = RoundedCornerShape(30.dp),
        )

        /*if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }*/
    }
}

/**
 * Preview for [AuthNotSecuredTextField].
 */
