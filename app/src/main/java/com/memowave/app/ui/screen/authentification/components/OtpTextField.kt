package com.memowave.app.ui.screen.authentification.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.memowave.app.ui.theme.MemowaveTheme

/**
 * Code-entry field rendered as [length] separate single-character boxes.
 * Backed by a single [value] string — typing in any cell advances focus to the
 * next, deleting moves focus back. Non-digit input is ignored.
 *
 * @param value Current value, length 0..[length], digits only
 * @param onValueChange Emits the updated value (still digits only, length ≤ [length])
 * @param length Number of cells; default 5 (matches backend OTP length)
 * @param isError Renders the cells in error colors
 * @param modifier Outer modifier (typically padding)
 */
@Composable
fun OtpTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    length: Int = 5,
    isError: Boolean = false,
) {
    val focusRequesters = remember(length) { List(length) { FocusRequester() } }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
    ) {
        for (index in 0 until length) {
            val cellChar = value.getOrNull(index)?.toString().orEmpty()

            OutlinedTextField(
                value = cellChar,
                onValueChange = { input ->
                    val sanitized = input.filter(Char::isDigit)
                    handleCellChange(
                        index = index,
                        currentValue = value,
                        sanitized = sanitized,
                        length = length,
                        focusRequesters = focusRequesters,
                        onValueChange = onValueChange,
                    )
                },
                modifier = Modifier
                    .size(width = 56.dp, height = 64.dp)
                    .focusRequester(focusRequesters[index]),
                singleLine = true,
                isError = isError,
                shape = RoundedCornerShape(16.dp),
                textStyle = LocalTextStyle.current.copy(
                    textAlign = TextAlign.Center,
                    fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.NumberPassword,
                    imeAction = if (index == length - 1) ImeAction.Done else ImeAction.Next,
                ),
                colors = OutlinedTextFieldDefaults.colors(),
            )
        }
    }
}

/**
 * Update the OTP value when a single cell's input changes.
 *
 * Three cases:
 *  - empty input: drop the char at [index] and any chars after, move focus left
 *  - one new digit: replace/insert at [index], move focus right
 *  - multi-char (paste/autofill): replace from [index] onwards, focus the last filled cell
 */
private fun handleCellChange(
    index: Int,
    currentValue: String,
    sanitized: String,
    length: Int,
    focusRequesters: List<FocusRequester>,
    onValueChange: (String) -> Unit,
) {
    when {
        sanitized.isEmpty() -> {
            val updated = currentValue.take(index)
            onValueChange(updated)
            if (index > 0) focusRequesters[index - 1].requestFocus()
        }
        sanitized.length == 1 -> {
            val before = currentValue.take(index)
            val after = currentValue.drop(index + 1)
            val merged = (before + sanitized + after).take(length)
            onValueChange(merged)
            if (index < length - 1) focusRequesters[index + 1].requestFocus()
        }
        else -> {
            val before = currentValue.take(index)
            val merged = (before + sanitized).take(length)
            onValueChange(merged)
            val targetIndex = (merged.length - 1).coerceAtMost(length - 1).coerceAtLeast(0)
            focusRequesters[targetIndex].requestFocus()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OtpTextFieldPreview() {
    MemowaveTheme {
        OtpTextField(value = "12", onValueChange = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun OtpTextFieldErrorPreview() {
    MemowaveTheme {
        OtpTextField(value = "12345", onValueChange = {}, isError = true)
    }
}
