package com.memowave.app.ui.screen.main_page

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.runtime.DisposableEffect
import androidx.navigation.NavController
import com.memowave.app.AppViewModel
import com.memowave.app.R
import com.memowave.app.ui.screen.main_page.components.BaseWordStatictics
import com.memowave.app.ui.screen.main_page.components.ContinueLearningButton
import com.memowave.app.ui.screen.main_page.components.InterestingFacts
import com.memowave.app.ui.screen.main_page.components.LearningMode
import com.memowave.app.ui.screen.main_page.components.StreakCard
import com.memowave.app.ui.navigation.Screen
import com.memowave.app.ui.theme.MemowaveTheme

@Composable
fun MainPageRoute(
    appViewModel: AppViewModel = hiltViewModel(),
    navController: NavController? = null,
    viewModel: MainPageViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) viewModel.refresh()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    MainPageScreen(
        appViewModel = appViewModel,
        navController = navController,
        state = state,
        onLaunchLastMode = {
            val mode = LEARNING_MODES.firstOrNull { it.id == state.lastLearningModeId }
                ?: LEARNING_MODES.first()
            viewModel.onLearningModeLaunched(mode.id)
            mode.route?.let { navController?.navigate(it) }
        },
        onLaunchMode = { mode ->
            viewModel.onLearningModeLaunched(mode.id)
            mode.route?.let { navController?.navigate(it) }
        },
        onPrevFact = { total -> viewModel.cycleFact(-1, total) },
        onNextFact = { total -> viewModel.cycleFact(+1, total) },
    )
}

data class LearningModeConfig(
    val id: String,
    val iconResId: Int,
    val route: String? = null,
)

internal val LEARNING_MODES = listOf(
    LearningModeConfig("Каротчки", R.drawable.playing_cards_24, route = Screen.Flashcard.route),
    LearningModeConfig("Перевод", R.drawable.round_translate_24),
    LearningModeConfig("Викторина", R.drawable.electric_bolt_24),
    LearningModeConfig("Слово-пазл", R.drawable.baseline_extension_24),
)

@Composable
fun MainPageScreen(
    appViewModel: AppViewModel? = null,
    navController: NavController? = null,
    state: MainPageUiState = MainPageUiState(),
    onLaunchLastMode: () -> Unit = {},
    onLaunchMode: (LearningModeConfig) -> Unit = {},
    onPrevFact: (Int) -> Unit = {},
    onNextFact: (Int) -> Unit = {},
) {
    val factTitles = stringArrayResource(R.array.language_fact_titles)
    val factBodies = stringArrayResource(R.array.language_fact_bodies)
    val factsCount = minOf(factTitles.size, factBodies.size)
    val safeIndex = if (factsCount > 0) state.factIndex.coerceIn(0, factsCount - 1) else 0
    val factTitle = factTitles.getOrNull(safeIndex).orEmpty()
    val factBody = factBodies.getOrNull(safeIndex).orEmpty()

    val lastMode = LEARNING_MODES.firstOrNull { it.id == state.lastLearningModeId }
        ?: LEARNING_MODES.first()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(top = 32.dp, bottom = 32.dp)
    ) {
        ContinueLearningButton(
            modeLabel = lastMode.id,
            modeIconResId = lastMode.iconResId,
            onClick = onLaunchLastMode,
        )
        StreakCard(
            state = state.streak,
            modifier = Modifier.padding(top = 20.dp),
        )
        BaseWordStatictics(
            modifier = Modifier.padding(top = 20.dp),
            newCount = state.newCount,
            dueCount = state.dueCount,
            learnedCount = state.learnedCount,
        )

        LEARNING_MODES.chunked(2).forEach { rowModes ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = if (rowModes == LEARNING_MODES.chunked(2).first()) 28.dp
                        else 12.dp
                    )
            ) {
                rowModes.forEachIndexed { index, mode ->
                    LearningMode(
                        modifier = Modifier
                            .weight(1f)
                            .padding(
                                start = if (index == 0) 0.dp
                                else 6.dp,
                                end = if (index == 1) 0.dp
                                else 6.dp
                            ),
                        cornerRadius = 30f,
                        figureSize = 250.dp,
                        modeName = mode.id,
                        iconResId = mode.iconResId,
                        onClick = { onLaunchMode(mode) }
                    )
                }
            }
        }

        InterestingFacts(
            modifier = Modifier.padding(top = 28.dp),
            title = factTitle,
            body = factBody,
            onPrev = { onPrevFact(factsCount) },
            onNext = { onNextFact(factsCount) },
        )
    }
}

@Preview(device = "spec:height=1500dp,width=411dp")
@Composable
fun MainPageScreenPreview() {
    MemowaveTheme {
        MainPageScreen()
    }
}
