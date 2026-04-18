package com.memowave.app.ui.screen.flashcard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memowave.app.domain.model.FlashcardResult
import com.memowave.app.domain.usecase.category.GetCategoriesUseCase
import com.memowave.app.domain.usecase.word.GetWordsByCategoryUseCase
import com.memowave.app.domain.usecase.word.GetWordsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val XP_PER_CORRECT = 25

@HiltViewModel
class FlashcardGameViewModel @Inject constructor(
    private val getWordsUseCase: GetWordsUseCase,
    private val getWordsByCategoryUseCase: GetWordsByCategoryUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FlashcardGameUiState())
    val uiState: StateFlow<FlashcardGameUiState> = _uiState.asStateFlow()

    fun onEvent(event: FlashcardGameEvent) {
        when (event) {
            is FlashcardGameEvent.LoadCategories -> loadCategories()
            is FlashcardGameEvent.SelectCategory -> {
                _uiState.value = _uiState.value.copy(selectedCategoryId = event.categoryId)
            }
            is FlashcardGameEvent.SetWordCount -> {
                _uiState.value = _uiState.value.copy(wordCount = event.count)
            }
            is FlashcardGameEvent.SetShuffled -> {
                _uiState.value = _uiState.value.copy(isShuffled = event.shuffled)
            }
            is FlashcardGameEvent.SetShowTranslationFirst -> {
                _uiState.value = _uiState.value.copy(showTranslationFirst = event.value)
            }
            is FlashcardGameEvent.StartGame -> startGame()
            is FlashcardGameEvent.FlipCard -> {
                _uiState.value = _uiState.value.copy(isCardFlipped = !_uiState.value.isCardFlipped)
            }
            is FlashcardGameEvent.MarkCorrect -> markAnswer(isCorrect = true)
            is FlashcardGameEvent.MarkWrong -> markAnswer(isCorrect = false)
            is FlashcardGameEvent.DismissXpPopup -> {
                _uiState.value = _uiState.value.copy(showXpPopup = false)
            }
            is FlashcardGameEvent.RestartGame -> restartGame()
            is FlashcardGameEvent.ToggleSettings -> {
                _uiState.value = _uiState.value.copy(
                    showSettingsSheet = !_uiState.value.showSettingsSheet
                )
            }
            is FlashcardGameEvent.RequestExit -> {
                if (_uiState.value.phase == FlashcardPhase.GAME && _uiState.value.results.isNotEmpty()) {
                    _uiState.value = _uiState.value.copy(showExitConfirmation = true)
                } else {
                    _uiState.value = _uiState.value.copy(showExitConfirmation = false)
                }
            }
            is FlashcardGameEvent.ConfirmExit -> {
                _uiState.value = _uiState.value.copy(showExitConfirmation = false)
            }
            is FlashcardGameEvent.DismissExitDialog -> {
                _uiState.value = _uiState.value.copy(showExitConfirmation = false)
            }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            getCategoriesUseCase().onSuccess { categories ->
                _uiState.value = _uiState.value.copy(categories = categories)
            }
        }
    }

    private fun startGame() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val categoryId = _uiState.value.selectedCategoryId
            val wordsResult = if (categoryId != null) {
                getWordsByCategoryUseCase(categoryId)
            } else {
                getWordsUseCase()
            }

            wordsResult.onSuccess { allWords ->
                if (allWords.isEmpty()) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Нет слов для изучения. Добавьте слова в библиотеку."
                    )
                    return@onSuccess
                }

                val processedWords = if (_uiState.value.isShuffled) {
                    allWords.shuffled()
                } else {
                    allWords
                }

                val wordCount = _uiState.value.wordCount
                val selectedWords = if (wordCount == 0) {
                    processedWords // 0 means "All"
                } else {
                    processedWords.take(wordCount)
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    words = selectedWords,
                    currentIndex = 0,
                    isCardFlipped = false,
                    results = emptyList(),
                    totalXpEarned = 0,
                    phase = FlashcardPhase.GAME
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "Ошибка загрузки слов"
                )
            }
        }
    }

    private fun markAnswer(isCorrect: Boolean) {
        val state = _uiState.value
        val currentWord = state.currentWord ?: return

        val result = FlashcardResult(wordId = currentWord.id, isCorrect = isCorrect)
        val xpGained = if (isCorrect) XP_PER_CORRECT else 0
        val newResults = state.results + result

        if (state.isLastCard) {
            _uiState.value = state.copy(
                results = newResults,
                totalXpEarned = state.totalXpEarned + xpGained,
                showXpPopup = isCorrect,
                xpPopupAmount = xpGained,
                phase = FlashcardPhase.SUMMARY
            )
        } else {
            _uiState.value = state.copy(
                results = newResults,
                totalXpEarned = state.totalXpEarned + xpGained,
                showXpPopup = isCorrect,
                xpPopupAmount = xpGained,
                currentIndex = state.currentIndex + 1,
                isCardFlipped = false
            )
        }
    }

    private fun restartGame() {
        _uiState.value = _uiState.value.copy(
            phase = FlashcardPhase.LOBBY,
            words = emptyList(),
            currentIndex = 0,
            isCardFlipped = false,
            results = emptyList(),
            totalXpEarned = 0,
            showXpPopup = false,
            errorMessage = null
        )
    }

    /**
     * Returns true if the back press should be consumed (exit dialog shown),
     * false if the caller should navigate back.
     */
    fun handleBackPress(): Boolean {
        return if (_uiState.value.phase == FlashcardPhase.GAME && _uiState.value.results.isNotEmpty()) {
            _uiState.value = _uiState.value.copy(showExitConfirmation = true)
            true
        } else {
            false
        }
    }
}
