package com.memowave.app.ui.screen.flashcard

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.memowave.app.AppViewModel
import com.memowave.app.R
import com.memowave.app.ui.common.MemowaveTopBar
import com.memowave.app.ui.common.notification.NotificationManager
import com.memowave.app.ui.screen.flashcard.components.AnimatedPlayButton
import com.memowave.app.ui.screen.flashcard.components.FlashcardActionButtons
import com.memowave.app.ui.screen.flashcard.components.FlashcardCard
import com.memowave.app.ui.screen.flashcard.components.FlashcardNumberedAnswerButtons
import com.memowave.app.ui.screen.flashcard.components.FlashcardSettingsSheet
import com.memowave.app.ui.screen.flashcard.components.FlashcardSummaryContent
import com.memowave.app.ui.screen.flashcard.components.FlashcardTopBar
import com.memowave.app.ui.screen.flashcard.components.XpPopup
import com.memowave.app.domain.model.Word
import com.memowave.app.ui.theme.MemowaveTheme

@Composable
fun FlashcardRoute(
    appViewModel: AppViewModel,
    navController: NavController
) {
    val viewModel: FlashcardGameViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onEvent(FlashcardGameEvent.LoadCategories)
    }

    BackHandler {
        if (!viewModel.handleBackPress()) {
            navController.popBackStack()
        }
    }

    FlashcardScreen(
        state = uiState,
        onEvent = viewModel::onEvent,
        onNavigateBack = { navController.popBackStack() },
        onWordCountLocked = {
            appViewModel.notificationManager.showWarning(
                "Количество слов нельзя менять — начните игру заново"
            )
        },
        onCategoryLocked = {
            appViewModel.notificationManager.showWarning(
                "Набор слов нельзя менять — начните игру заново"
            )
        },
        notificationManager = appViewModel.notificationManager
    )
}

@Composable
private fun FlashcardScreen(
    state: FlashcardGameUiState,
    onEvent: (FlashcardGameEvent) -> Unit,
    onNavigateBack: () -> Unit,
    onWordCountLocked: () -> Unit,
    onCategoryLocked: () -> Unit,
    notificationManager: NotificationManager? = null
) {
    Box(modifier = Modifier.fillMaxSize()) {
        when (state.phase) {
            FlashcardPhase.LOBBY -> FlashcardLobbyContent(
                onStartGame = { onEvent(FlashcardGameEvent.StartGame) },
                onSettingsClick = { onEvent(FlashcardGameEvent.ToggleSettings) },
                onBackClick = onNavigateBack,
                isLoading = state.isLoading,
                errorMessage = state.errorMessage
            )

            FlashcardPhase.GAME -> FlashcardGameContent(
                state = state,
                onEvent = onEvent,
                onBackClick = {
                    onEvent(FlashcardGameEvent.RequestExit)
                    if (!state.showExitConfirmation && state.results.isEmpty()) {
                        onNavigateBack()
                    }
                }
            )

            FlashcardPhase.SUMMARY -> FlashcardSummaryContent(
                correctCount = state.correctCount,
                wrongCount = state.wrongCount,
                totalXp = state.totalXpEarned,
                totalWords = state.words.size,
                onPlayAgain = { onEvent(FlashcardGameEvent.RestartGame) },
                onGoBack = onNavigateBack
            )
        }

        // Settings bottom sheet (overlay on any phase)
        if (state.showSettingsSheet) {
            FlashcardSettingsSheet(
                wordCount = state.wordCount,
                isShuffled = state.isShuffled,
                gamePhase = state.phase,
                gameMode = state.gameMode,
                showTranslationFirst = state.showTranslationFirst,
                selectedCategoryId = state.selectedCategoryId,
                categories = state.categories,
                onWordCountChanged = { onEvent(FlashcardGameEvent.SetWordCount(it)) },
                onShuffledChanged = { onEvent(FlashcardGameEvent.SetShuffled(it)) },
                onGameModeChanged = { onEvent(FlashcardGameEvent.ChangeGameMode(it)) },
                onShowTranslationFirstChanged = {
                    onEvent(
                        FlashcardGameEvent.SetShowTranslationFirst(
                            it
                        )
                    )
                },
                onCategorySelected = { onEvent(FlashcardGameEvent.SelectCategory(it)) },
                onWordCountLocked = onWordCountLocked,
                onCategoryLocked = onCategoryLocked,
                onDismiss = { onEvent(FlashcardGameEvent.ToggleSettings) },
                notificationManager = notificationManager
            )
        }

        // Exit confirmation dialog
        if (state.showExitConfirmation) {
            AlertDialog(
                onDismissRequest = { onEvent(FlashcardGameEvent.DismissExitDialog) },
                shape = RoundedCornerShape(28.dp),
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                icon = {
                    Icon(
                        modifier = Modifier.size(36.dp),
                        painter = painterResource(R.drawable.round_warning_24),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                },
                title = {
                    Text(
                        text = "Выйти из игры?",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.W600
                    )
                },
                text = {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Прогресс текущей сессии не сохранится.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                },
                confirmButton = {
                    Button(
                        modifier = Modifier.height(48.dp),
                        onClick = { onEvent(FlashcardGameEvent.DismissExitDialog) },
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(
                            text = "Остаться",
                            fontWeight = FontWeight.W600
                        )
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        modifier = Modifier.height(48.dp),
                        onClick = {
                            onEvent(FlashcardGameEvent.ConfirmExit)
                            onNavigateBack()
                        },
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text(
                            text = "Выйти",
                            fontWeight = FontWeight.W600
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun FlashcardLobbyContent(
    onStartGame: () -> Unit,
    onSettingsClick: () -> Unit,
    onBackClick: () -> Unit,
    isLoading: Boolean,
    errorMessage: String?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        MemowaveTopBar(onBackClick = onBackClick)

        // Center content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Карточки",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.W600
            )

            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = "Запоминай слова с помощью карточек",
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

            // Settings button
            IconButton(
                modifier = Modifier
                    .padding(top = 24.dp)
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

@Composable
private fun FlashcardGameContent(
    state: FlashcardGameUiState,
    onEvent: (FlashcardGameEvent) -> Unit,
    onBackClick: () -> Unit
) {
    val currentWord = state.currentWord ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        FlashcardTopBar(
            modifier = Modifier.padding(top = 12.dp),
            progress = state.progress,
            totalXp = state.totalXpEarned,
            onBackClick = onBackClick,
            onSettingsClick = { onEvent(FlashcardGameEvent.ToggleSettings) }
        )

        // XP popup
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            contentAlignment = Alignment.Center
        ) {

            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    // Card
                    FlashcardCard(
                        word = currentWord,
                        isFlipped = state.isCardFlipped,
                        showTranslationFirst = state.showTranslationFirst,
                        onClick = {
                            when (state.gameMode) {
                                FlashcardGameMode.BINARY -> onEvent(FlashcardGameEvent.FlipCard)
                                FlashcardGameMode.NUMBERED -> {
                                    if (state.selectedAnswerIndex != null) {
                                        onEvent(FlashcardGameEvent.AdvanceCard)
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(top = 16.dp)
                    )
                }
                // Answer controls
                when (state.gameMode) {
                    FlashcardGameMode.BINARY -> FlashcardActionButtons(
                        isEnabled = state.isCardFlipped,
                        onWrongClick = { onEvent(FlashcardGameEvent.MarkWrong) },
                        onCorrectClick = { onEvent(FlashcardGameEvent.MarkCorrect) },
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(vertical = 24.dp)
                    )
                    FlashcardGameMode.NUMBERED -> FlashcardNumberedAnswerButtons(
                        options = state.numberedOptions,
                        correctIndex = state.correctAnswerIndex,
                        selectedIndex = state.selectedAnswerIndex,
                        onSelect = { onEvent(FlashcardGameEvent.SelectAnswer(it)) },
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(vertical = 24.dp)
                    )
                }
            }

            XpPopup(
                modifier = Modifier.align(Alignment.TopCenter),
                visible = state.showXpPopup,
                amount = state.xpPopupAmount,
                onDismiss = { onEvent(FlashcardGameEvent.DismissXpPopup) }
            )
        }
    }
}

// --- Previews ---

private val previewWords = listOf(
    Word(
        id = 1L, original = "Unforgettable", translation = "Незабываемый",
        examples = listOf("A visit to the museum is an unforgettable experience.")
    ),
    Word(
        id = 2L, original = "Serendipity", translation = "Интуитивная прозорливость",
        examples = listOf("Finding that book was pure serendipity.")
    ),
    Word(
        id = 3L, original = "Eloquent", translation = "Красноречивый",
        examples = listOf("She gave an eloquent speech.")
    )
)

@Preview(showBackground = true, device = "spec:height=900dp,width=411dp")
@Composable
private fun FlashcardLobbyPreview() {
    MemowaveTheme {
        FlashcardScreen(
            state = FlashcardGameUiState(phase = FlashcardPhase.LOBBY),
            onEvent = {},
            onNavigateBack = {},
            onWordCountLocked = {},
            onCategoryLocked = {}
        )
    }
}

@Preview(showBackground = true, device = "spec:height=900dp,width=411dp")
@Composable
private fun FlashcardGamePreview() {
    MemowaveTheme {
        FlashcardScreen(
            state = FlashcardGameUiState(
                phase = FlashcardPhase.GAME,
                words = previewWords,
                currentIndex = 0,
                isCardFlipped = false,
                totalXpEarned = 50
            ),
            onEvent = {},
            onNavigateBack = {},
            onWordCountLocked = {},
            onCategoryLocked = {}
        )
    }
}

@Preview(showBackground = true, device = "spec:height=900dp,width=411dp")
@Composable
private fun FlashcardGameFlippedPreview() {
    MemowaveTheme {
        FlashcardScreen(
            state = FlashcardGameUiState(
                phase = FlashcardPhase.GAME,
                words = previewWords,
                currentIndex = 1,
                isCardFlipped = true,
                totalXpEarned = 75
            ),
            onEvent = {},
            onNavigateBack = {},
            onWordCountLocked = {},
            onCategoryLocked = {}
        )
    }
}

@Preview(showBackground = true, device = "spec:height=900dp,width=411dp")
@Composable
private fun FlashcardSummaryPreview() {
    MemowaveTheme {
        FlashcardScreen(
            state = FlashcardGameUiState(
                phase = FlashcardPhase.SUMMARY,
                words = previewWords,
                totalXpEarned = 300,
                results = listOf(
                    com.memowave.app.domain.model.FlashcardResult(1L, true),
                    com.memowave.app.domain.model.FlashcardResult(2L, false),
                    com.memowave.app.domain.model.FlashcardResult(3L, true)
                )
            ),
            onEvent = {},
            onNavigateBack = {},
            onWordCountLocked = {},
            onCategoryLocked = {}
        )
    }
}
