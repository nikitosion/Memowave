package com.memowave.app.ui.screen.learning_shared.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.memowave.app.R
import com.memowave.app.domain.model.Category
import com.memowave.app.ui.common.MemowaveTopBar
import com.memowave.app.ui.screen.learning_shared.LoadState

/**
 * Generic LOBBY layout shared by every learning mode (Flashcard, Translation, Quiz).
 * Shows mode title + subtitle, the animated Play button, the category selector,
 * and a settings gear. The mode supplies its own copy via [title] / [subtitle].
 */
@Composable
fun LearningLobbyContent(
    title: String,
    subtitle: String,
    onStartGame: () -> Unit,
    onSettingsClick: () -> Unit,
    onBackClick: () -> Unit,
    isLoading: Boolean,
    errorMessage: String?,
    categories: List<Category>,
    selectedCategoryId: Long?,
    categoryWordCounts: Map<Long, Int>,
    totalWordsCount: Int,
    categoriesLoadState: LoadState,
    onCategorySelected: (Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        MemowaveTopBar(onBackClick = onBackClick)

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            val containerHeight = maxHeight
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .heightIn(min = containerHeight)
                    .padding(horizontal = 16.dp)
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.W600
                )

                Text(
                    modifier = Modifier.padding(top = 8.dp),
                    text = subtitle,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.outline
                )

                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(top = 40.dp)
                            .size(48.dp)
                    )
                } else {
                    AnimatedPlayButton(
                        onClick = onStartGame,
                        modifier = Modifier.padding(top = 40.dp)
                    )
                }

                if (errorMessage != null) {
                    Text(
                        modifier = Modifier.padding(top = 16.dp),
                        text = errorMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                CategorySelector(
                    modifier = Modifier.padding(top = 24.dp),
                    categories = categories,
                    selectedCategoryId = selectedCategoryId,
                    wordCounts = categoryWordCounts,
                    totalWordsCount = totalWordsCount,
                    loadState = categoriesLoadState,
                    onCategorySelected = onCategorySelected
                )

                IconButton(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .background(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = CircleShape
                        ),
                    onClick = onSettingsClick
                ) {
                    Icon(
                        modifier = Modifier.size(26.dp),
                        painter = painterResource(R.drawable.round_settings_24),
                        contentDescription = "Настройки",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}
