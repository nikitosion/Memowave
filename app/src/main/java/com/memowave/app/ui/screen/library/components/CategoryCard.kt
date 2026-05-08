package com.memowave.app.ui.screen.library.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.memowave.app.domain.model.Category
import com.memowave.app.ui.common.category.CategoryIcons
import com.memowave.app.ui.common.category.CategoryPalette
import com.memowave.app.ui.theme.MemowaveTheme

@Composable
fun CategoryCard(
    category: Category,
    wordsCount: Int,
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                CategoryIconBadge(
                    color = category.color,
                    iconName = category.iconName
                )
                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.W600,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = wordsCountLabel(wordsCount),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryIconBadge(
    color: String?,
    iconName: String?
) {
    val parsed = CategoryPalette.parseHexColor(color)
    val container = parsed ?: MaterialTheme.colorScheme.primaryContainer
    val content = if (parsed != null) Color.White else MaterialTheme.colorScheme.onPrimaryContainer
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(container),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(CategoryIcons.resolveDrawable(iconName)),
            contentDescription = null,
            tint = content,
            modifier = Modifier.size(20.dp)
        )
    }
}

/**
 * Returns "1 word" / "5 words" — keeps Russian-language plurals approximate.
 * Kept English to avoid hard-coding plural rules; switch to `pluralStringResource`
 * if precise localisation is required.
 */
private fun wordsCountLabel(count: Int): String = when (count) {
    1 -> "1 word"
    else -> "$count words"
}

@Preview(showBackground = true)
@Composable
private fun CategoryCardPreview() {
    MemowaveTheme {
        CategoryCard(
            category = Category(
                id = 1,
                name = "Природа",
                color = "#FF22C55E",
                iconName = "globe"
            ),
            wordsCount = 42,
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
