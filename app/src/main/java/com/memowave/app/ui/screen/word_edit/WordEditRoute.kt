package com.memowave.app.ui.screen.word_edit

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController

@Composable
fun WordEditRoute(
    navController: NavController,
    viewModel: WordEditViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.isFinished) {
        if (state.isFinished) {
            navController.popBackStack()
        }
    }

    BackHandler(enabled = !state.isBusy) {
        viewModel.onEvent(WordEditEvent.CancelRequested)
    }

    WordEditScreen(
        state = state,
        onEvent = viewModel::onEvent,
        onUploadImage = viewModel::uploadPickedImage
    )
}
