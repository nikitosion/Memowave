package com.memowave.app.ui.screen.translation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.memowave.app.AppViewModel
import com.memowave.app.R
import com.memowave.app.ui.screen.flashcard.components.FlashcardTopBar
import com.memowave.app.ui.screen.learning_shared.LearningPhase
import com.memowave.app.ui.screen.learning_shared.components.LearningLobbyContent
import com.memowave.app.ui.screen.learning_shared.components.LearningNextPrepContent
import com.memowave.app.ui.screen.learning_shared.components.LearningSummaryContent
import com.memowave.app.ui.screen.learning_shared.components.XpPopup
import com.memowave.app.ui.screen.translation.components.TranslationGameContent
import com.memowave.app.ui.screen.translation.components.TranslationSettingsSheet

@Composable
fun TranslationRoute(
    appViewModel: AppViewModel,
    navController: NavController
) {
    val viewModel: TranslationGameViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onEvent(TranslationGameEvent.LoadCategories)
    }

    BackHandler {
        if (!viewModel.handleBackPress()) {
            navController.popBackStack()
        }
    }

    TranslationScreen(
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
        }
    )
}

@Composable
private fun TranslationScreen(
    state: TranslationGameUiState,
    onEvent: (TranslationGameEvent) -> Unit,
    onNavigateBack: () -> Unit,
    onWordCountLocked: () -> Unit,
    onCategoryLocked: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        when (state.phase) {
            LearningPhase.LOBBY -> LearningLobbyContent(
                title = stringResource(R.string.translation_lobby_title),
                subtitle = stringResource(R.string.translation_lobby_subtitle),
                onStartGame = { onEvent(TranslationGameEvent.StartGame) },
                onSettingsClick = { onEvent(TranslationGameEvent.ShowSettings) },
                onBackClick = onNavigateBack,
                isLoading = state.isLoading,
                errorMessage = state.errorMessage,
                categories = state.categories,
                selectedCategoryId = state.selectedCategoryId,
                categoryWordCounts = state.categoryWordCounts,
                totalWordsCount = state.totalWordsCount,
                categoriesLoadState = state.categoriesLoadState,
                onCategorySelected = { onEvent(TranslationGameEvent.SelectCategory(it)) }
            )

            LearningPhase.GAME -> TranslationGamePhase(
                state = state,
                onEvent = onEvent,
                onBackClick = {
                    onEvent(TranslationGameEvent.RequestExit)
                    if (!state.showExitConfirmation && state.results.isEmpty()) {
                        onNavigateBack()
                    }
                }
            )

            LearningPhase.SUMMARY -> LearningSummaryContent(
                correctCount = state.correctCount,
                wrongCount = state.wrongCount,
                totalXp = state.totalXpEarned,
                totalWords = state.words.size,
                summaries = state.summaries,
                streakWordsRemaining = 0,
                onPlayAgain = { onEvent(TranslationGameEvent.EnterNextPrep) },
                onGoBack = onNavigateBack
            )

            LearningPhase.NEXT_PREP -> LearningNextPrepContent(
                categories = state.categories,
                selectedCategoryId = state.selectedCategoryId,
                categoryWordCounts = state.categoryWordCounts,
                totalWordsCount = state.totalWordsCount,
                categoriesLoadState = state.categoriesLoadState,
                selectedCategoryHasWords = state.selectedCategoryHasWords,
                countdownSeconds = state.nextPrepCountdownSeconds,
                onCategorySelected = { onEvent(TranslationGameEvent.SelectCategory(it)) },
                onSettingsClick = { onEvent(TranslationGameEvent.ShowSettings) },
                onContinueClick = { onEvent(TranslationGameEvent.ConfirmNextSession) },
                onBackClick = onNavigateBack
            )
        }

        if (state.showSettingsSheet) {
            TranslationSettingsSheet(
                wordCount = state.wordCount,
                isShuffled = state.isShuffled,
                typoStrictness = state.typoStrictness,
                gamePhase = state.phase,
                selectedCategoryId = state.selectedCategoryId,
                categories = state.categories,
                onWordCountChanged = { onEvent(TranslationGameEvent.SetWordCount(it)) },
                onShuffledChanged = { onEvent(TranslationGameEvent.SetShuffled(it)) },
                onStrictnessChanged = { onEvent(TranslationGameEvent.SetTypoStrictness(it)) },
                onCategorySelected = { onEvent(TranslationGameEvent.SelectCategory(it)) },
                onWordCountLocked = onWordCountLocked,
                onCategoryLocked = onCategoryLocked,
                onDismiss = { onEvent(TranslationGameEvent.DismissSettings) }
            )
        }

        if (state.showExitConfirmation) {
            AlertDialog(
                onDismissRequest = { onEvent(TranslationGameEvent.DismissExitDialog) },
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
                        onClick = { onEvent(TranslationGameEvent.DismissExitDialog) },
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) { Text(text = "Остаться", fontWeight = FontWeight.W600) }
                },
                dismissButton = {
                    OutlinedButton(
                        modifier = Modifier.height(48.dp),
                        onClick = {
                            onEvent(TranslationGameEvent.ConfirmExit)
                            onNavigateBack()
                        },
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) { Text(text = "Выйти", fontWeight = FontWeight.W600) }
                }
            )
        }
    }
}

@Composable
private fun TranslationGamePhase(
    state: TranslationGameUiState,
    onEvent: (TranslationGameEvent) -> Unit,
    onBackClick: () -> Unit
) {
    val word = state.currentWord ?: return

    Column(modifier = Modifier.fillMaxSize()) {
        FlashcardTopBar(
            modifier = Modifier.padding(top = 12.dp),
            progress = state.progress,
            totalXp = state.totalXpEarned,
            onBackClick = onBackClick,
            onSettingsClick = { onEvent(TranslationGameEvent.ShowSettings) }
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp)
        ) {
            TranslationGameContent(
                word = word,
                userInput = state.userInput,
                isAwaitingFeedback = state.isAwaitingFeedback,
                checkResult = state.checkResult,
                hintLettersShown = state.hintLettersShown,
                answerRevealed = state.answerRevealed,
                onInputChange = { onEvent(TranslationGameEvent.UpdateInput(it)) },
                onSubmit = { onEvent(TranslationGameEvent.SubmitAnswer) },
                onShowHint = { onEvent(TranslationGameEvent.ShowHintLetter) },
                onRevealAnswer = { onEvent(TranslationGameEvent.RevealAnswer) }
            )

            XpPopup(
                modifier = Modifier.align(Alignment.TopCenter),
                visible = state.showXpPopup,
                amount = state.xpPopupAmount,
                onDismiss = { onEvent(TranslationGameEvent.DismissXpPopup) }
            )
        }
    }
}
