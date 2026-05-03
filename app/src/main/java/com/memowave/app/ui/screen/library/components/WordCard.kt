package com.memowave.app.ui.screen.library.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.memowave.app.R
import com.memowave.app.domain.model.Category
import com.memowave.app.domain.model.Word
import com.memowave.app.ui.common.media.LocalImageUrlResolver
import com.memowave.app.ui.theme.MemowaveTheme

@Composable
fun WordCard(
    word: Word,
    category: Category?,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            word.imageUrl?.let { fileName ->
                val resolver = LocalImageUrlResolver.current
                val context = LocalContext.current
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(120.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(resolver.resolve(fileName))
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.size(12.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    if (category != null) {
                        Box(
                            modifier = Modifier
                                .padding(bottom = 4.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = category.name,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = word.original,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.W600,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (word.isSynced) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.error
                                )
                        )
                    }
                    Text(
                        text = word.translation,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (word.examples.isNotEmpty() && word.examples[0].isNotBlank()) {
                        Text(
                            text = word.examples[0],
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(onClick = onEditClick) {
                        Icon(
                            modifier = Modifier.background(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = CircleShape
                            ).padding(8.dp),
                            painter = painterResource(R.drawable.round_edit_24),
                            contentDescription = "Edit word",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun WordCardPreview() {
    val mockWord = Word(
        id = 1,
        original = "Apple",
        translation = "Яблоко",
        categoryId = 1,
        examples = listOf("I eat an apple every day.")
    )
    val mockCategory = Category(
        id = 1,
        name = "Фрукты",
        description = "Фрукты и ягоды"
    )
    MemowaveTheme {
        WordCard(
            word = mockWord,
            category = mockCategory,
            onEditClick = {},
            onDeleteClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
