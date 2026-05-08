package com.memowave.app.ui.screen.flashcard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.memowave.app.R
import com.memowave.app.domain.model.Category
import com.memowave.app.ui.common.MemowaveTopBar
import com.memowave.app.ui.screen.flashcard.LoadState
import com.memowave.app.ui.theme.MemowaveTheme

/**
 * Phase between SUMMARY and the next session: header, subtitle, category
 * selector, optional empty-category hint, the [CountdownContinueButton] with a
 * 5s drain timer, and a settings gear. Any user interaction (selector tap,
 * settings tap, back) cancels the timer in the ViewModel.
 */
@Composable
fun FlashcardNextPrepContent(
    categories: List<Category>,
    selectedCategoryId: Long?,
    categoryWordCounts: Map<Long, Int>,
    totalWordsCount: Int,
    categoriesLoadState: LoadState,
    selectedCategoryHasWords: Boolean,
    countdownSeconds: Int?,
    onCategorySelected: (Long?) -> Unit,
    onSettingsClick: () -> Unit,
    onContinueClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        MemowaveTopBar(onBackClick = onBackClick)

        // Middle area is scrollable so an expanded category list never pushes
        // the bottom CTA + settings off-screen on shorter devices.
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(top = 12.dp, bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.flashcard_next_prep_title),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.W600,
                textAlign = TextAlign.Center
            )
            Text(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .padding(horizontal = 8.dp),
                text = stringResource(R.string.flashcard_next_prep_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.outline,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(32.dp))

            CategorySelector(
                categories = categories,
                selectedCategoryId = selectedCategoryId,
                wordCounts = categoryWordCounts,
                totalWordsCount = totalWordsCount,
                loadState = categoriesLoadState,
                onCategorySelected = onCategorySelected
            )

            if (!selectedCategoryHasWords) {
                Text(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .padding(horizontal = 8.dp),
                    text = stringResource(R.string.flashcard_next_prep_empty_hint),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CountdownContinueButton(
                text = stringResource(R.string.flashcard_next_prep_continue),
                countdownSeconds = countdownSeconds,
                onClick = onContinueClick,
                enabled = selectedCategoryHasWords
            )

            IconButton(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = CircleShape
                    ),
                onClick = onSettingsClick
            ) {
                Icon(
                    modifier = Modifier.size(26.dp),
                    painter = painterResource(R.drawable.round_settings_24),
                    contentDescription = stringResource(R.string.settings_title),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}

// --- Previews ---

private val previewCategories = listOf(
    Category(id = 1L, name = "Базовые", description = "Самые частые слова"),
    Category(id = 2L, name = "Еда", description = "Продукты и блюда"),
    Category(id = 3L, name = "Пустая", description = "Пока без слов")
)

@Preview(showBackground = true, device = "spec:height=900dp,width=411dp")
@Composable
private fun FlashcardNextPrepContentRunningPreview() {
    MemowaveTheme {
        FlashcardNextPrepContent(
            categories = previewCategories,
            selectedCategoryId = 1L,
            categoryWordCounts = mapOf(1L to 24, 2L to 12, 3L to 0),
            totalWordsCount = 36,
            categoriesLoadState = LoadState.LOADED,
            selectedCategoryHasWords = true,
            countdownSeconds = 4,
            onCategorySelected = {},
            onSettingsClick = {},
            onContinueClick = {},
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true, device = "spec:height=900dp,width=411dp")
@Composable
private fun FlashcardNextPrepContentEmptyCategoryPreview() {
    MemowaveTheme {
        FlashcardNextPrepContent(
            categories = previewCategories,
            selectedCategoryId = 3L,
            categoryWordCounts = mapOf(1L to 24, 2L to 12, 3L to 0),
            totalWordsCount = 36,
            categoriesLoadState = LoadState.LOADED,
            selectedCategoryHasWords = false,
            countdownSeconds = null,
            onCategorySelected = {},
            onSettingsClick = {},
            onContinueClick = {},
            onBackClick = {}
        )
    }
}
