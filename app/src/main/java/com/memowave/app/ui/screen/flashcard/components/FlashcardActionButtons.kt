package com.memowave.app.ui.screen.flashcard.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.memowave.app.R
import com.memowave.app.ui.theme.MemowaveTheme

@Composable
fun FlashcardActionButtons(
    isEnabled: Boolean,
    onWrongClick: () -> Unit,
    onCorrectClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Wrong button
        OutlinedButton(
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            onClick = onWrongClick,
            enabled = isEnabled,
            shape = RoundedCornerShape(30.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Icon(
                painter = painterResource(R.drawable.round_close_24),
                contentDescription = null,
                modifier = Modifier.size(22.dp)
            )
            Text(
                modifier = Modifier.weight(1f, fill = false).padding(start = 4.dp),
                text = "Неправильно",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.W600
            )
        }

        // Correct button
        Button(
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            onClick = onCorrectClick,
            enabled = isEnabled,
            shape = RoundedCornerShape(30.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Icon(
                painter = painterResource(R.drawable.round_check_24),
                contentDescription = null,
                modifier = Modifier.size(22.dp)
            )
            Text(
                modifier = Modifier.weight(1f, fill = false).padding(start = 4.dp),
                text = "Правильно",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.W600
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FlashcardActionButtonsEnabledPreview() {
    MemowaveTheme {
        FlashcardActionButtons(
            isEnabled = true,
            onWrongClick = {},
            onCorrectClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FlashcardActionButtonsDisabledPreview() {
    MemowaveTheme {
        FlashcardActionButtons(
            isEnabled = false,
            onWrongClick = {},
            onCorrectClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
