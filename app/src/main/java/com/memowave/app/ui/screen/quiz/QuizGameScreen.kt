package com.memowave.app.ui.screen.quiz

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
import com.memowave.app.ui.common.MemowaveTopBar
import com.memowave.app.ui.screen.learning_shared.LearningPhase
import com.memowave.app.ui.screen.learning_shared.components.LearningLobbyContent
import com.memowave.app.ui.screen.learning_shared.components.LearningNextPrepContent
import com.memowave.app.ui.screen.learning_shared.components.LearningSummaryContent
import com.memowave.app.ui.screen.learning_shared.components.XpPopup
import com.memowave.app.ui.screen.quiz.components.QuizGameContent
import com.memowave.app.ui.screen.quiz.components.QuizSettingsSheet
import com.memowave.app.ui.screen.quiz.components.QuizTimerBar

@Composable
fun QuizRoute(
    appViewModel: AppViewModel,
    navController: NavController
) {
    val viewModel: QuizGameViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onEvent(QuizGameEvent.LoadCategories)
    }

    BackHandler {
        if (!viewModel.handleBackPress()) {
            navController.popBackStack()
        }
    }

    QuizScreen(
        state = uiState,
        onEvent = viewModel::onEvent,
        onNavigateBack = { navController.popBackStack() },
        onCategoryLocked = {
            appViewModel.notificationManager.showWarning(
                "Набор слов нельзя менять — начните игру заново"
            )
        },
        onDurationLocked = {
            appViewModel.notificationManager.showWarning(
                "Длительность нельзя менять во время игры"
            )
        }
    )
}

@Composable
private fun QuizScreen(
    state: QuizGameUiState,
    onEvent: (QuizGameEvent) -> Unit,
    onNavigateBack: () -> Unit,
    onCategoryLocked: () -> Unit,
    onDurationLocked: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        when (state.phase) {
            LearningPhase.LOBBY -> LearningLobbyContent(
                title = stringResource(R.string.quiz_lobby_title),
                subtitle = stringResource(R.string.quiz_lobby_subtitle, state.totalDurationSeconds),
                onStartGame = { onEvent(QuizGameEvent.StartGame) },
                onSettingsClick = { onEvent(QuizGameEvent.ShowSettings) },
                onBackClick = onNavigateBack,
                isLoading = state.isLoading,
                errorMessage = state.errorMessage,
                categories = state.categories,
                selectedCategoryId = state.selectedCategoryId,
                categoryWordCounts = state.categoryWordCounts,
                totalWordsCount = state.totalWordsCount,
                categoriesLoadState = state.categoriesLoadState,
                onCategorySelected = { onEvent(QuizGameEvent.SelectCategory(it)) }
            )

            LearningPhase.GAME -> QuizGamePhase(
                state = state,
                onEvent = onEvent,
                onBackClick = {
                    onEvent(QuizGameEvent.RequestExit)
                    if (!state.showExitConfirmation && state.results.isEmpty()) {
                        onNavigateBack()
                    }
                }
            )

            LearningPhase.SUMMARY -> LearningSummaryContent(
                correctCount = state.correctCount,
                wrongCount = state.wrongCount,
                totalXp = state.totalXpEarned,
                totalWords = state.totalAnswered,
                summaries = state.summaries,
                streakWordsRemaining = 0,
                onPlayAgain = { onEvent(QuizGameEvent.EnterNextPrep) },
                onGoBack = onNavigateBack
            )

            LearningPhase.NEXT_PREP -> LearningNextPrepContent(
                categories = state.categories,
                selectedCategoryId = state.selectedCategoryId,
                categoryWordCounts = state.categoryWordCounts,
                totalWordsCount = state.totalWordsCount,
                categoriesLoadState = state.categoriesLoadState,
                selectedCategoryHasWords = state.canStart,
                countdownSeconds = state.nextPrepCountdownSeconds,
                onCategorySelected = { onEvent(QuizGameEvent.SelectCategory(it)) },
                onSettingsClick = { onEvent(QuizGameEvent.ShowSettings) },
                onContinueClick = { onEvent(QuizGameEvent.ConfirmNextSession) },
                onBackClick = onNavigateBack
            )
        }

        if (state.showSettingsSheet) {
            QuizSettingsSheet(
                durationSeconds = state.totalDurationSeconds,
                isShuffled = state.isShuffled,
                gamePhase = state.phase,
                selectedCategoryId = state.selectedCategoryId,
                categories = state.categories,
                onDurationChanged = { onEvent(QuizGameEvent.SetDurationSeconds(it)) },
                onShuffledChanged = { onEvent(QuizGameEvent.SetShuffled(it)) },
                onCategorySelected = { onEvent(QuizGameEvent.SelectCategory(it)) },
                onCategoryLocked = onCategoryLocked,
                onDurationLocked = onDurationLocked,
                onDismiss = { onEvent(QuizGameEvent.DismissSettings) }
            )
        }

        if (state.showExitConfirmation) {
            AlertDialog(
                onDismissRequest = { onEvent(QuizGameEvent.DismissExitDialog) },
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
                        onClick = { onEvent(QuizGameEvent.DismissExitDialog) },
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
                            onEvent(QuizGameEvent.ConfirmExit)
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
private fun QuizGamePhase(
    state: QuizGameUiState,
    onEvent: (QuizGameEvent) -> Unit,
    onBackClick: () -> Unit
) {
    val word = state.currentWord ?: return

    Column(modifier = Modifier.fillMaxSize()) {
        MemowaveTopBar(onBackClick = onBackClick)

        QuizTimerBar(
            modifier = Modifier.padding(top = 8.dp),
            remainingMillis = state.remainingMillis,
            totalMillis = state.totalDurationSeconds * 1000L,
            streak = state.streak
        )

        Box(modifier = Modifier.fillMaxSize()) {
            QuizGameContent(
                word = word,
                options = state.options,
                correctOptionIndex = state.correctOptionIndex,
                selectedOptionIndex = state.selectedOptionIndex,
                onSelect = { onEvent(QuizGameEvent.SelectOption(it)) }
            )

            XpPopup(
                modifier = Modifier.align(Alignment.TopCenter),
                visible = state.showXpPopup,
                amount = state.xpPopupAmount,
                onDismiss = { onEvent(QuizGameEvent.DismissXpPopup) }
            )
        }
    }
}
