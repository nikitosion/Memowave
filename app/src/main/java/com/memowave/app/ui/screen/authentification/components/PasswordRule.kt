package com.memowave.app.ui.screen.authentification.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.memowave.app.R
import com.memowave.app.ui.theme.MemowaveTheme

/**
 * Visual indicator for a password rule (e.g., min length, digit, etc.).
 *
 * @param rule Description of the password rule
 * @param isSatisfied Whether the rule is satisfied
 * @param modifier Modifier for styling
 */
@Composable
fun PasswordRule(
    modifier: Modifier = Modifier,
    rule: String,
    isSatisfied: Boolean?
) {
    val color = if (isSatisfied == null) {
        MaterialTheme.colorScheme.onSurfaceVariant
    } else if (isSatisfied) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.error
    }


    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
    ) {
        androidx.compose.material3.Icon(
            painter = androidx.compose.ui.res.painterResource(
                id = if (isSatisfied == null || isSatisfied) {
                    R.drawable.round_check_circle_24
                } else {
                    R.drawable.round_cancel_24
                }
            ),
            contentDescription = null,
            tint = color,
            modifier = androidx.compose.ui.Modifier
                .size(16.dp)
        )
        androidx.compose.material3.Text(
            text = rule,
            color = color,
            style = MaterialTheme.typography.bodyMedium,
            modifier = androidx.compose.ui.Modifier
                .padding(start = 8.dp)
        )
    }
}

/**
 * Preview for [PasswordRule].
 */
@Preview
@Composable
fun PasswordRulePreview() {
    MemowaveTheme {
        PasswordRule(
            rule = "Содержит более 8 символов",
            isSatisfied = null
        )
    }
}