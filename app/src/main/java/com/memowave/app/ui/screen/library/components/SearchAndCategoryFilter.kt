package com.memowave.app.ui.screen.library.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.memowave.app.domain.model.Category
import androidx.compose.ui.tooling.preview.Preview
import com.memowave.app.ui.theme.MemowaveTheme

@Composable
fun SearchAndCategoryFilter(
    searchQuery: String,
    selectedCategoryId: Long?,
    categories: List<Category>,
    onSearchChange: (String) -> Unit,
    onCategorySelected: (Long?) -> Unit,
    onAddWordClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Search words") },
            singleLine = true,
            shape = RoundedCornerShape(30.dp)
        )

        if (categories.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CategoryChip(
                    text = "All",
                    isSelected = selectedCategoryId == null,
                    onClick = { onCategorySelected(null) }
                )
                categories.forEach { category ->
                    CategoryChip(
                        text = category.name,
                        isSelected = selectedCategoryId == category.id,
                        onClick = { onCategorySelected(category.id) }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchAndCategoryFilterPreview() {
    val mockCategories = listOf(
        Category(id = 1, name = "Фрукты"),
        Category(id = 2, name = "Овощи")
    )
    MemowaveTheme {
        SearchAndCategoryFilter(
            searchQuery = "",
            selectedCategoryId = null,
            categories = mockCategories,
            onSearchChange = {},
            onCategorySelected = {},
            onAddWordClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
