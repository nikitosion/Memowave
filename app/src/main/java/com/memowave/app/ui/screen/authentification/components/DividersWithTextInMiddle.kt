package com.memowave.app.ui.screen.authentification.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Horizontal divider with centered text, used to separate sections in authentication screens.
 *
 * @param text Text to display in the center
 * @param modifier Modifier for styling
 */
@Composable
fun DividersWithTextInMiddle(
    text: String = "ИЛИ",
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.outlineVariant
        )
        Text(
            modifier = Modifier.padding(horizontal = 8.dp),
            text = "ИЛИ",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.outlineVariant
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}

/**
 * Preview for [DividersWithTextInMiddle].
 */
@Composable
fun PreviewDividersWithTextInMiddle() {
    DividersWithTextInMiddle()
}
