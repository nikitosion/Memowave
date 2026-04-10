package com.memowave.app.ui.screen.library.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.memowave.app.domain.model.Category
import com.memowave.app.domain.model.Word
import kotlin.collections.forEach

@Composable
fun WordEditDialog(
    categories: List<Category>,
    initialWord: Word?,
    onDismiss: () -> Unit,
    onSave: (String, String, Long?, List<String>, String?) -> Unit
) {
    var original by remember { mutableStateOf(initialWord?.original.orEmpty()) }
    var translation by remember { mutableStateOf(initialWord?.translation.orEmpty()) }
    var example by remember { mutableStateOf(initialWord?.examples[0].orEmpty()) }
    var note by remember { mutableStateOf(initialWord?.note.orEmpty()) }
    var selectedCategoryId by remember { mutableStateOf(initialWord?.categoryId) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (initialWord == null) "Новое слово" else "Редактировать слово",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = original,
                    onValueChange = { original = it },
                    label = { Text("Оригинал") },
                    singleLine = true,
                    shape = RoundedCornerShape(30.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = translation,
                    onValueChange = { translation = it },
                    label = { Text("Перевод") },
                    singleLine = true,
                    shape = RoundedCornerShape(30.dp),
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth()
                )
                OutlinedTextField(
                    value = example,
                    onValueChange = { example = it },
                    label = { Text("Пример (необязательно)") },
                    shape = RoundedCornerShape(30.dp),
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth()
                )
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Заметка (необязательно)") },
                    shape = RoundedCornerShape(30.dp),
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth()
                )

                if (categories.isNotEmpty()) {
                    Text(
                        text = "Категория",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                    )
                    Row {
                        CategoryChip(
                            text = "Без категории",
                            isSelected = selectedCategoryId == null,
                            onClick = { selectedCategoryId = null }
                        )
                    }
                    Column(
                        modifier = Modifier
                            .padding(top = 8.dp)
                    ) {
                        categories.forEach { category ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedCategoryId = category.id }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(
                                            color = if (selectedCategoryId == category.id) {
                                                MaterialTheme.colorScheme.primary
                                            } else {
                                                MaterialTheme.colorScheme.surfaceContainer
                                            }
                                        )
                                )
                                Text(
                                    text = category.name,
                                    modifier = Modifier.padding(start = 8.dp),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = original.isNotBlank() && translation.isNotBlank(),
                onClick = {
                    onSave(
                        original,
                        translation,
                        selectedCategoryId,
                        listOf(example),
                        note.ifBlank { null }
                    )
                }
            ) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}