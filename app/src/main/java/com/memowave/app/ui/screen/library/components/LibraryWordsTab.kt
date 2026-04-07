package com.memowave.app.ui.screen.library.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.memowave.app.domain.model.Category
import com.memowave.app.domain.model.Word
import com.memowave.app.ui.screen.library.LibraryEvent
import com.memowave.app.ui.screen.library.LibraryUiState
import com.memowave.app.ui.theme.MemowaveTheme

@Composable
fun LibraryWordsTab(
    state: LibraryUiState,
    onEvent: (LibraryEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val filteredWords = state.words.filter { word ->
        val matchesCategory =
            state.selectedCategoryId?.let { word.categoryId == it } ?: true
        val query = state.searchQuery.trim()
        val matchesSearch = if (query.isBlank()) {
            true
        } else {
            word.original.contains(query, ignoreCase = true) ||
                    word.translation.contains(query, ignoreCase = true)
        }
        matchesCategory && matchesSearch
    }

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            SearchAndCategoryFilter(
                searchQuery = state.searchQuery,
                selectedCategoryId = state.selectedCategoryId,
                categories = state.categories,
                onSearchChange = { query -> onEvent(LibraryEvent.SearchChanged(query)) },
                onCategorySelected = { categoryId -> onEvent(LibraryEvent.CategorySelected(categoryId)) },
                onAddWordClick = { onEvent(LibraryEvent.AddWordClicked) }
            )
        }

        if (filteredWords.isEmpty()) {
            item {
                EmptyStateCard(
                    modifier = Modifier.padding(top = 12.dp),
                    title = "Нет слов",
                    subtitle = "Добавьте первое слово, чтобы начать учить",
                    primaryActionText = "Добавить слово",
                    onPrimaryActionClick = { onEvent(LibraryEvent.AddWordClicked) }
                )
            }
        } else {
            items(filteredWords) { word ->
                WordCard(
                    word = word,
                    category = state.categories.firstOrNull { it.id == word.categoryId },
                    onEditClick = { onEvent(LibraryEvent.EditWordClicked(word)) },
                    onDeleteClick = { onEvent(LibraryEvent.DeleteWordClicked(word.id)) }
                )
            }
        }
    }
}

@Preview(device = "spec:height=1500dp,width=411dp", showSystemUi = false, showBackground = true)
@Composable
fun LibraryWordsTabPreview() {
    val mockCategories = listOf(
        Category(id = 1, name = "Фрукты", description = "Фрукты и ягоды"),
        Category(id = 2, name = "Овощи", description = "Овощи и зелень")
    )
    val mockWords = listOf(
        Word(id = 1, original = "Apple", translation = "Яблоко", categoryId = 1, examples = listOf("I eat an apple every day.")),
        Word(id = 2, original = "Carrot", translation = "Морковь", categoryId = 2, examples = listOf("Carrots are orange.")),
        Word(id = 3, original = "Banana", translation = "Банан", categoryId = 1, examples = listOf("Bananas are yellow."))
    )
    MemowaveTheme {
        LibraryWordsTab(
            state = LibraryUiState(
                words = mockWords,
                categories = mockCategories,
                selectedCategoryId = null,
                searchQuery = ""
            ),
            onEvent = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
