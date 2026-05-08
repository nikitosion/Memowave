package com.memowave.app.ui.screen.flashcard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.memowave.app.R
import com.memowave.app.ui.theme.MemowaveTheme

@Composable
fun NewWordBadge(
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        text = stringResource(R.string.flashcard_badge_new),
        style = MaterialTheme.typography.titleMedium.copy(
            fontFamily = FontFamily(Font(R.font.bagelfatone_regular))
        ),
        fontWeight = FontWeight.W700,
        color = MaterialTheme.colorScheme.primaryContainer
    )
}

@Preview(showBackground = true)
@Composable
private fun NewWordBadgePreview() {
    MemowaveTheme {
        NewWordBadge(modifier = Modifier.padding(8.dp))
    }
}
