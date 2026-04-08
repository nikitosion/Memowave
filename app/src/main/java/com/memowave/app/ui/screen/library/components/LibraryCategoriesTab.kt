package com.memowave.app.ui.screen.library.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.memowave.app.ui.screen.library.LibraryEvent
import com.memowave.app.ui.screen.library.LibraryUiState

@Composable
fun LibraryCategoriesTab(
    state: LibraryUiState,
    onEvent: (LibraryEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Категории",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.W600
                )
                OutlinedButton(onClick = { onEvent(LibraryEvent.AddCategoryClicked) }) {
                    Text("Новая категория")
                }
            }
        }

        if (state.categories.isEmpty()) {
            item {
                EmptyStateCard(
                    modifier = Modifier.padding(top = 4.dp),
                    title = "Категории не созданы",
                    subtitle = "Создайте категории, чтобы лучше организовать свои слова",
                    primaryActionText = "Создать категорию",
                    onPrimaryActionClick = { onEvent(LibraryEvent.AddCategoryClicked) }
                )
            }
        } else {
            items(state.categories) { category ->
                CategoryCard(
                    category = category,
                    wordsCount = state.words.count { it.categoryId == category.id },
                    onEditClick = { onEvent(LibraryEvent.EditCategoryClicked(category)) },
                    onDeleteClick = { onEvent(LibraryEvent.DeleteCategoryClicked(category.id)) }
                )
            }
        }
    }
}