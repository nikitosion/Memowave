package com.memowave.app.ui.screen.library.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.memowave.app.R
import com.memowave.app.domain.model.Category
import com.memowave.app.domain.model.Word
import com.memowave.app.ui.theme.MemowaveTheme

@Composable
fun WordCard(
    word: Word,
    category: Category?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            val bottomTextEndPadding = if (!word.isSynced) 20.dp else 0.dp
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = word.original,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.W600,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    if (category != null) {
                        Spacer(Modifier.width(8.dp))
                        CategoryTag(category)
                    }
                }
                Text(
                    text = word.translation,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp, end = bottomTextEndPadding)
                )
                val example = word.examples.firstOrNull().orEmpty()
                if (example.isNotBlank()) {
                    Text(
                        text = example,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline,
                        fontStyle = FontStyle.Italic,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, end = bottomTextEndPadding)
                    )
                }
            }

            if (!word.isSynced) {
                Icon(
                    painter = painterResource(R.drawable.round_cloud_off_24),
                    contentDescription = stringResource(R.string.word_card_a11y_not_synced),
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 12.dp, bottom = 10.dp)
                        .size(14.dp)
                )
            }
        }
    }
}

@Composable
private fun CategoryTag(category: Category) {
    val accent = parseHexColor(category.color)
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (accent != null) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(accent)
            )
            Spacer(Modifier.width(6.dp))
        }
        Text(
            text = category.name,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

private fun parseHexColor(hex: String?): Color? {
    if (hex.isNullOrBlank()) return null
    return runCatching {
        Color(android.graphics.Color.parseColor(hex))
    }.getOrNull()
}

@Preview(showBackground = true, name = "WordCard — with category")
@Composable
private fun WordCardPreview() {
    MemowaveTheme {
        WordCard(
            word = Word(
                id = 1,
                original = "Apple",
                translation = "Яблоко",
                categoryId = 1,
                examples = listOf("I eat an apple every day.")
            ),
            category = Category(id = 1, name = "Фрукты", color = "#F87171"),
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "WordCard — not synced")
@Composable
private fun WordCardPreviewNotSynced() {
    MemowaveTheme {
        WordCard(
            word = Word(
                id = 2,
                original = "ocean",
                translation = "океан",
                isSynced = false
            ),
            category = null,
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "WordCard — long category")
@Composable
private fun WordCardPreviewLong() {
    MemowaveTheme {
        WordCard(
            word = Word(
                id = 3,
                original = "ephemeral",
                translation = "мимолётный",
                examples = listOf("The fame was ephemeral and faded within a week.")
            ),
            category = Category(id = 1, name = "Прилагательные", color = "#60A5FA"),
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
