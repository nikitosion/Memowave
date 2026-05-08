package com.memowave.app.ui.screen.flashcard.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.memowave.app.domain.model.Word
import com.memowave.app.ui.common.media.LocalImageUrlResolver
import com.memowave.app.ui.theme.MemowaveTheme

@Composable
fun FlashcardCard(
    word: Word,
    isFlipped: Boolean,
    showTranslationFirst: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isNewWord: Boolean = false
) {
    val density = LocalDensity.current

    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "cardFlip"
    )

    val isFrontVisible = rotation < 90f

    val frontText = if (showTranslationFirst) word.translation else word.original
    val backText = if (showTranslationFirst) word.original else word.translation
    val frontLabel = if (showTranslationFirst) "Перевод" else "Слово"
    val backLabel = if (showTranslationFirst) "Слово" else "Перевод"

    Card(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 200.dp, max = 520.dp)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density.density
            }
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick
            ),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (isFrontVisible) {
                // Front side
                CardFrontContent(
                    word = word,
                    displayText = frontText,
                    label = frontLabel,
                    showTranslationFirst = showTranslationFirst,
                    imageFileName = word.imageUrl,
                    isNewWord = isNewWord
                )
            } else {
                // Back side (mirrored due to rotation)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { rotationY = 180f },
                    contentAlignment = Alignment.Center
                ) {
                    CardBackContent(
                        displayText = backText,
                        label = backLabel,
                        translatedWord = frontText,
                        imageFileName = word.imageUrl,
                        isNewWord = isNewWord
                    )
                }
            }
        }
    }
}

@Composable
private fun CardFrontContent(
    word: Word,
    displayText: String,
    label: String,
    showTranslationFirst: Boolean,
    imageFileName: String?,
    isNewWord: Boolean
) {
    Column(
        modifier = Modifier

            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        FlashcardImage(imageFileName)

        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.outline
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = displayText,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.W600,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (isNewWord) {
                Spacer(Modifier.width(12.dp))
                NewWordBadge()
            }
        }

        // Transcription slot (for future use)
        // word.transcription?.let { ... }

        // Example sentence with the word underlined
        if (word.examples.isNotEmpty() && !showTranslationFirst) {
            val example = word.examples.first()
            val annotatedExample = buildAnnotatedString {
                val lowerExample = example.lowercase()
                val lowerWord = word.original.lowercase()
                val startIndex = lowerExample.indexOf(lowerWord)

                if (startIndex >= 0) {
                    append(example.substring(0, startIndex))
                    withStyle(
                        SpanStyle(
                            textDecoration = TextDecoration.Underline,
                            fontWeight = FontWeight.W600
                        )
                    ) {
                        append(example.substring(startIndex, startIndex + word.original.length))
                    }
                    append(example.substring(startIndex + word.original.length))
                } else {
                    append(example)
                }
            }

            Text(
                modifier = Modifier.padding(top = 12.dp),
                text = annotatedExample,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.8f)
            )
        }

        Text(
            modifier = Modifier.padding(top = 16.dp),
            text = "Нажмите, чтобы перевернуть",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
        )
    }
}

@Composable
private fun CardBackContent(
    displayText: String,
    label: String,
    translatedWord: String,
    imageFileName: String?,
    isNewWord: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        FlashcardImage(imageFileName)

        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.outline
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = displayText,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.W600,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (isNewWord) {
                Spacer(Modifier.width(12.dp))
                NewWordBadge()
            }
        }

        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = translatedWord,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
private fun FlashcardImage(fileName: String?) {
    if (fileName.isNullOrBlank()) return
    val resolver = LocalImageUrlResolver.current
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .size(160.dp)
            .clip(RoundedCornerShape(24.dp))
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

private val previewWord = Word(
    id = 1L,
    original = "Unforgettable",
    translation = "Незабываемый",
    examples = listOf("A visit to the museum is an unforgettable experience.")
)

@Preview(showBackground = true)
@Composable
private fun FlashcardCardFrontPreview() {
    MemowaveTheme {
        FlashcardCard(
            word = previewWord,
            isFlipped = false,
            showTranslationFirst = false,
            onClick = {},
            modifier = Modifier.padding(16.dp),
            isNewWord = true
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FlashcardCardFlippedPreview() {
    MemowaveTheme {
        FlashcardCard(
            word = previewWord,
            isFlipped = true,
            showTranslationFirst = false,
            onClick = {},
            modifier = Modifier.padding(16.dp),
            isNewWord = true
        )
    }
}
