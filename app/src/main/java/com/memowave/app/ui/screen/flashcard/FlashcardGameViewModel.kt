package com.memowave.app.ui.screen.flashcard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memowave.app.domain.algorithm.CardPhase
import com.memowave.app.domain.model.FlashcardResult
import com.memowave.app.domain.model.Rating
import com.memowave.app.domain.model.Word
import com.memowave.app.domain.usecase.category.GetCategoriesUseCase
import com.memowave.app.domain.usecase.word.CalculateGradePreviewUseCase
import com.memowave.app.domain.usecase.word.GetWordsByCategoryUseCase
import com.memowave.app.domain.usecase.word.GetWordsUseCase
import com.memowave.app.domain.usecase.word.UpdateWordProgressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val AUTO_ADVANCE_MILLIS = 2200L
private const val NEXT_PREP_TOTAL_SECONDS = 5
private val STREAK_TARGET_RANGE = 1..7

private val XP_BY_RATING = mapOf(
    Rating.Again to 0,
    Rating.Hard to 15,
    Rating.Good to 25,
    Rating.Easy to 35
)

@HiltViewModel
class FlashcardGameViewModel @Inject constructor(
    private val getWordsUseCase: GetWordsUseCase,
    private val getWordsByCategoryUseCase: GetWordsByCategoryUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val updateWordProgressUseCase: UpdateWordProgressUseCase,
    private val calculateGradePreviewUseCase: CalculateGradePreviewUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FlashcardGameUiState())
    val uiState: StateFlow<FlashcardGameUiState> = _uiState.asStateFlow()

    private var allWordsCache: List<Word>? = null
    private var advanceJob: Job? = null
    private var nextPrepJob: Job? = null

    fun onEvent(event: FlashcardGameEvent) {
        when (event) {
            is FlashcardGameEvent.LoadCategories -> loadCategories()
            is FlashcardGameEvent.SelectCategory -> {
                _uiState.value = _uiState.value.copy(selectedCategoryId = event.categoryId)
                if (_uiState.value.phase == FlashcardPhase.NEXT_PREP) {
                    cancelNextPrepTimer()
                }
            }
            is FlashcardGameEvent.ChangeGameMode -> changeGameMode(event.gameMode)
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
            is FlashcardGameEvent.MarkRating -> applyAnswer(event.rating)
            is FlashcardGameEvent.SelectAnswer -> selectNumberedAnswer(event.index)
            is FlashcardGameEvent.AdvanceCard -> advanceCard()
            is FlashcardGameEvent.DismissXpPopup -> {
                _uiState.value = _uiState.value.copy(showXpPopup = false)
            }
            is FlashcardGameEvent.RestartGame -> restartGame()
            is FlashcardGameEvent.ShowSettings -> {
                if (_uiState.value.phase == FlashcardPhase.NEXT_PREP) {
                    cancelNextPrepTimer()
                }
                if (!_uiState.value.showSettingsSheet) {
                    _uiState.value = _uiState.value.copy(showSettingsSheet = true)
                }
            }
            is FlashcardGameEvent.DismissSettings -> {
                if (_uiState.value.showSettingsSheet) {
                    _uiState.value = _uiState.value.copy(showSettingsSheet = false)
                }
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
            is FlashcardGameEvent.EnterNextPrep -> enterNextPrep()
            is FlashcardGameEvent.CancelNextPrepTimer -> cancelNextPrepTimer()
            is FlashcardGameEvent.ConfirmNextSession -> confirmNextSession()
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(categoriesLoadState = LoadState.LOADING)
            val categoriesResult = getCategoriesUseCase()
            val wordsResult = getWordsUseCase()
            if (categoriesResult.isFailure || wordsResult.isFailure) {
                _uiState.value = _uiState.value.copy(categoriesLoadState = LoadState.ERROR)
                return@launch
            }
            val categories = categoriesResult.getOrNull() ?: emptyList()
            val allWords = wordsResult.getOrNull().orEmpty()
            allWordsCache = allWords
            val counts = allWords
                .mapNotNull { it.categoryId }
                .groupingBy { it }
                .eachCount()
            _uiState.value = _uiState.value.copy(
                categories = categories,
                categoryWordCounts = counts,
                totalWordsCount = allWords.size,
                categoriesLoadState = LoadState.LOADED
            )
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

                val firstWord = selectedWords.first()
                val (options, correctIdx) = if (_uiState.value.gameMode == FlashcardGameMode.NUMBERED) {
                    generateNumberedOptions(firstWord, selectedWords)
                } else {
                    emptyList<String>() to 0
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    words = selectedWords,
                    currentIndex = 0,
                    isCardFlipped = false,
                    results = emptyList(),
                    summaries = emptyList(),
                    totalXpEarned = 0,
                    phase = FlashcardPhase.GAME,
                    numberedOptions = options,
                    correctAnswerIndex = correctIdx,
                    selectedAnswerIndex = null,
                    progressDelta = null,
                    gradePreview = null,
                    streakWordsRemaining = 0,
                    nextPrepCountdownSeconds = null
                )
                computeGradePreview()
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "Ошибка загрузки слов"
                )
            }
        }
    }

    private fun selectNumberedAnswer(index: Int) {
        val state = _uiState.value
        if (state.selectedAnswerIndex != null) return
        val isCorrect = index == state.correctAnswerIndex
        _uiState.value = state.copy(selectedAnswerIndex = index)
        applyAnswer(if (isCorrect) Rating.Good else Rating.Again)
    }

    private fun applyAnswer(rating: Rating) {
        val currentWord = _uiState.value.currentWord ?: return
        val wasNew = currentWord.phase == CardPhase.Added.value
        val xpGained = XP_BY_RATING[rating] ?: 0
        val isCorrect = rating != Rating.Again
        recordResult(currentWord, isCorrect = isCorrect, xp = xpGained)
        _uiState.value = _uiState.value.copy(isCardFlipped = true)
        viewModelScope.launch {
            updateWordProgressUseCase(currentWord, rating)
                .onSuccess { newWord ->
                    if (_uiState.value.currentWord?.id == currentWord.id) {
                        val delta = WordProgressDelta.from(currentWord, newWord, wasNew)
                        val summary = FlashcardWordSummary(
                            word = currentWord,
                            isCorrect = isCorrect,
                            delta = delta
                        )
                        _uiState.value = _uiState.value.copy(
                            progressDelta = delta,
                            summaries = _uiState.value.summaries + summary
                        )
                    }
                }
        }
        scheduleAutoAdvance()
    }

    private fun recordResult(currentWord: Word, isCorrect: Boolean, xp: Int) {
        val state = _uiState.value
        val result = FlashcardResult(wordId = currentWord.id, isCorrect = isCorrect)
        _uiState.value = state.copy(
            results = state.results + result,
            totalXpEarned = state.totalXpEarned + xp,
            showXpPopup = xp > 0,
            xpPopupAmount = xp
        )
    }

    private fun computeGradePreview() {
        val word = _uiState.value.currentWord ?: return
        _uiState.value = _uiState.value.copy(
            gradePreview = calculateGradePreviewUseCase(word)
        )
    }

    private fun scheduleAutoAdvance() {
        advanceJob?.cancel()
        advanceJob = viewModelScope.launch {
            delay(AUTO_ADVANCE_MILLIS)
            advanceCard()
        }
    }

    private fun advanceCard() {
        advanceJob?.cancel()
        val state = _uiState.value
        if (state.phase != FlashcardPhase.GAME) return

        if (state.isLastCard) {
            _uiState.value = state.copy(
                phase = FlashcardPhase.SUMMARY,
                streakWordsRemaining = STREAK_TARGET_RANGE.random()
            )
        } else {
            moveToNextCard()
        }
    }

    private fun moveToNextCard() {
        val state = _uiState.value
        val nextIdx = state.currentIndex + 1
        val nextWord = state.words.getOrNull(nextIdx) ?: return

        viewModelScope.launch {
            val (options, correctIdx) = if (state.gameMode == FlashcardGameMode.NUMBERED) {
                generateNumberedOptions(nextWord, state.words)
            } else {
                emptyList<String>() to 0
            }
            _uiState.value = _uiState.value.copy(
                currentIndex = nextIdx,
                isCardFlipped = false,
                selectedAnswerIndex = null,
                numberedOptions = options,
                correctAnswerIndex = correctIdx,
                progressDelta = null,
                gradePreview = null
            )
            computeGradePreview()
        }
    }

    private fun changeGameMode(mode: FlashcardGameMode) {
        val state = _uiState.value
        if (state.gameMode == mode) return

        advanceJob?.cancel()

        if (mode == FlashcardGameMode.NUMBERED && state.phase == FlashcardPhase.GAME) {
            val currentWord = state.currentWord
            if (currentWord != null) {
                viewModelScope.launch {
                    val (options, correctIdx) = generateNumberedOptions(currentWord, state.words)
                    _uiState.value = _uiState.value.copy(
                        gameMode = mode,
                        numberedOptions = options,
                        correctAnswerIndex = correctIdx,
                        selectedAnswerIndex = null,
                        isCardFlipped = false
                    )
                }
                return
            }
        }

        _uiState.value = state.copy(
            gameMode = mode,
            selectedAnswerIndex = null
        )
    }

    private suspend fun generateNumberedOptions(
        currentWord: Word,
        gameWords: List<Word>
    ): Pair<List<String>, Int> {
        val inGamePool = gameWords.filter { it.id != currentWord.id }
        val pool = if (inGamePool.size >= 3) {
            inGamePool
        } else {
            loadAllWordsPool().filter { it.id != currentWord.id }
        }
        val distractors = pool.shuffled().take(3).map { it.translation }
        // If still fewer than 3 unique distractors available in the whole DB, pad with
        // a best-effort reuse — rare edge case (user has <4 words total).
        val paddedDistractors = if (distractors.size < 3) {
            val filler = List(3 - distractors.size) { "—" }
            distractors + filler
        } else distractors

        val correctIdx = (0..3).random()
        val options = paddedDistractors.toMutableList().apply { add(correctIdx, currentWord.translation) }
        return options to correctIdx
    }

    private suspend fun loadAllWordsPool(): List<Word> {
        allWordsCache?.let { return it }
        val all = getWordsUseCase().getOrNull().orEmpty()
        allWordsCache = all
        return all
    }

    private fun restartGame() {
        advanceJob?.cancel()
        cancelNextPrepTimer()
        _uiState.value = _uiState.value.copy(
            phase = FlashcardPhase.LOBBY,
            words = emptyList(),
            currentIndex = 0,
            isCardFlipped = false,
            results = emptyList(),
            summaries = emptyList(),
            totalXpEarned = 0,
            showXpPopup = false,
            errorMessage = null,
            numberedOptions = emptyList(),
            correctAnswerIndex = 0,
            selectedAnswerIndex = null,
            progressDelta = null,
            gradePreview = null,
            streakWordsRemaining = 0
        )
    }

    private fun enterNextPrep() {
        advanceJob?.cancel()
        cancelNextPrepTimer()
        _uiState.value = _uiState.value.copy(phase = FlashcardPhase.NEXT_PREP)
        if (_uiState.value.selectedCategoryHasWords) {
            startNextPrepTimer()
        }
    }

    private fun startNextPrepTimer() {
        nextPrepJob?.cancel()
        _uiState.value = _uiState.value.copy(nextPrepCountdownSeconds = NEXT_PREP_TOTAL_SECONDS)
        nextPrepJob = viewModelScope.launch {
            for (s in NEXT_PREP_TOTAL_SECONDS downTo 1) {
                _uiState.value = _uiState.value.copy(nextPrepCountdownSeconds = s)
                delay(1000)
            }
            if (_uiState.value.phase == FlashcardPhase.NEXT_PREP &&
                _uiState.value.selectedCategoryHasWords
            ) {
                confirmNextSession()
            }
        }
    }

    private fun cancelNextPrepTimer() {
        nextPrepJob?.cancel()
        nextPrepJob = null
        if (_uiState.value.nextPrepCountdownSeconds != null) {
            _uiState.value = _uiState.value.copy(nextPrepCountdownSeconds = null)
        }
    }

    private fun confirmNextSession() {
        cancelNextPrepTimer()
        // startGame() resets summaries/streak/etc. and switches phase to GAME
        startGame()
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
