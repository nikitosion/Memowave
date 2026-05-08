package com.memowave.app.ui.screen.category_edit

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.memowave.app.ui.navigation.Screen

@Composable
fun CategoryEditRoute(
    navController: NavController,
    viewModel: CategoryEditViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.isFinished) {
        if (state.isFinished) {
            navController.popBackStack()
        }
    }

    BackHandler(enabled = !state.isBusy) {
        viewModel.onEvent(CategoryEditEvent.CancelRequested)
    }

    CategoryEditScreen(
        state = state,
        onEvent = viewModel::onEvent,
        onWordClick = { wordId ->
            navController.navigate(Screen.WordEdit.routeFor(wordId))
        }
    )
}
