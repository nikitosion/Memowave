package com.memowave.app.ui.screen.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavController
import com.memowave.app.R
import com.memowave.app.domain.model.Word
import com.memowave.app.ui.navigation.Screen
import com.memowave.app.ui.screen.library.components.CategoryEditDialog
import com.memowave.app.ui.screen.library.components.LibraryCategoriesTab
import com.memowave.app.ui.screen.library.components.LibraryWordsTab
import com.memowave.app.ui.theme.MemowaveTheme

@Composable
fun LibraryRoute(
    navController: NavController,
    viewModel: LibraryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.onEvent(LibraryEvent.Load)
    }

    LibraryScreen(
        state = uiState,
        onEvent = { event ->
            when (event) {
                is LibraryEvent.AddWordClicked ->
                    navController.navigate(Screen.WordNew.route)
                is LibraryEvent.EditWordClicked ->
                    navController.navigate(Screen.WordEdit.routeFor(event.word.id))
                else -> viewModel.onEvent(event)
            }
        }
    )
}

private enum class LibraryTab {
    WORDS,
    CATEGORIES
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    state: LibraryUiState,
    onEvent: (LibraryEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(LibraryTab.WORDS) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp, bottom = 0.dp)
        ) {
            // Tabs
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                val segButtonHeight = 50.dp
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                    modifier = Modifier.height(segButtonHeight),
                    selected = selectedTab == LibraryTab.WORDS,
                    onClick = { selectedTab = LibraryTab.WORDS },
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.brand_family_24),
                            contentDescription = "Words"
                        )
                    }
                ) {
                    Text("Words")
                }
                SegmentedButton(
                    modifier = Modifier.height(segButtonHeight),
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                    selected = selectedTab == LibraryTab.CATEGORIES,
                    onClick = { selectedTab = LibraryTab.CATEGORIES },
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.round_emoji_symbols_24),
                            contentDescription = "Categories"
                        )
                    }
                ) {
                    Text("Categories")
                }
            }

            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                when (selectedTab) {
                    LibraryTab.WORDS -> LibraryWordsTab(
                        state = state,
                        onEvent = onEvent,
                        modifier = Modifier
                            .weight(1f)
                            .padding(top = 24.dp)
                    )

                    LibraryTab.CATEGORIES -> LibraryCategoriesTab(
                        state = state,
                        onEvent = onEvent,
                        modifier = Modifier
                            .weight(1f)
                            .padding(top = 24.dp)
                    )
                }
            }
        }

        FloatingActionButton(
            modifier = Modifier
                .padding(bottom = 16.dp, end = 16.dp)
                .align(Alignment.BottomEnd),
            onClick = {
                when (selectedTab) {
                    LibraryTab.WORDS -> onEvent(LibraryEvent.AddWordClicked)
                    LibraryTab.CATEGORIES -> onEvent(LibraryEvent.AddCategoryClicked)
                }
            },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(
                painter = painterResource(R.drawable.round_add_24),
                contentDescription = "Add"
            )
        }
    }

    if (state.isCategoryDialogOpen) {
        CategoryEditDialog(
            initialCategory = state.editingCategory,
            onDismiss = { onEvent(LibraryEvent.DismissCategoryDialog) },
            onSave = { name, description, colorHex ->
                onEvent(
                    LibraryEvent.SaveCategory(
                        name = name,
                        description = description,
                        colorHex = colorHex
                    )
                )
            }
        )
    }
}

@Preview(device = "spec:width=411dp,height=891dp", showSystemUi = false, showBackground = true)
@Composable
private fun LibraryScreenWordsPreview() {
    MemowaveTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
            ) { paddingValues ->
                Surface(
                    modifier = Modifier
                        .background(color = MaterialTheme.colorScheme.background)
                        .fillMaxSize()
                        .padding(
                            top = paddingValues.calculateTopPadding(),
                            bottom = paddingValues.calculateBottomPadding()
                        ),
                ) {
                    LibraryScreen(
                        state = LibraryUiState(
                            words = listOf(
                                Word(
                                    id = 1L,
                                    original = "hello",
                                    translation = "привет",
                                    categoryId = 1,
                                    examples = listOf("Hello, how are you?")
                                ),
                                Word(
                                    id = 2L,
                                    original = "ocean",
                                    translation = "океан",
                                    categoryId = 2,
                                    examples = listOf("The ocean is deep and blue.")
                                )
                            )
                        ),
                        onEvent = {}
                    )
                }
            }
        }
    }
}

@Preview(device = "spec:width=411dp,height=891dp", showSystemUi = false, showBackground = true)
@Composable
private fun LibraryScreenWithoutWordsPreview() {
    MemowaveTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
            ) { paddingValues ->
                Surface(
                    modifier = Modifier
                        .background(color = MaterialTheme.colorScheme.background)
                        .fillMaxSize()
                        .padding(
                            top = paddingValues.calculateTopPadding(),
                            bottom = paddingValues.calculateBottomPadding()
                        ),
                ) {
                    LibraryScreen(
                        state = LibraryUiState(
                            words = emptyList()
                        ),
                        onEvent = {}
                    )
                }
            }
        }
    }
}
