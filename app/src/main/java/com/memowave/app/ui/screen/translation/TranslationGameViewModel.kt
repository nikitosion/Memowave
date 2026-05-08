package com.memowave.app.ui.screen.translation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memowave.app.core.util.text.StringSimilarity
import com.memowave.app.domain.algorithm.CardPhase
import com.memowave.app.domain.model.FlashcardResult
import com.memowave.app.domain.model.Rating
import com.memowave.app.domain.model.Word
import com.memowave.app.domain.repository.SettingsRepository
import com.memowave.app.domain.usecase.category.GetCategoriesUseCase
import com.memowave.app.domain.usecase.streak.RecordWordReviewUseCase
import com.memowave.app.domain.usecase.word.CalculateGradePreviewUseCase
import com.memowave.app.domain.usecase.word.GetWordsByCategoryUseCase
import com.memowave.app.domain.usecase.word.GetWordsUseCase
import com.memowave.app.domain.usecase.word.UpdateWordProgressUseCase
import com.memowave.app.ui.screen.learning_shared.LearningPhase
import com.memowave.app.ui.screen.learning_shared.LearningWordSummary
import com.memowave.app.ui.screen.learning_shared.LoadState
import com.memowave.app.ui.screen.learning_shared.WordProgressDelta
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private const val FEEDBACK_VISIBLE_MILLIS = 2200L
private const val NEXT_PREP_TOTAL_SECONDS = 5
private const val FAST_ANSWER_THRESHOLD_MILLIS = 4000L

private val XP_BY_RATING = mapOf(
    Rating.Again to 0,
    Rating.Hard to 15,
    Rating.Good to 25,
    Rating.Easy to 35
)

@HiltViewModel
class TranslationGameViewModel @Inject constructor(
    private val getWordsUseCase: GetWordsUseCase,
    private val getWordsByCategoryUseCase: GetWordsByCategoryUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val updateWordProgressUseCase: UpdateWordProgressUseCase,
    private val calculateGradePreviewUseCase: CalculateGradePreviewUseCase,
    private val settingsRepository: SettingsRepository,
    private val recordWordReviewUseCase: RecordWordReviewUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TranslationGameUiState())
    val uiState: StateFlow<TranslationGameUiState> = _uiState.asStateFlow()

    private var advanceJob: Job? = null
    private var nextPrepJob: Job? = null

    init {
        viewModelScope.launch {
            val saved = settingsRepository.getSettings().first().lastTranslationStrictness
            val strictness = runCatching { TypoStrictness.valueOf(saved) }
                .getOrDefault(TypoStrictness.NORMAL)
            if (_uiState.value.typoStrictness != strictness) {
                _uiState.value = _uiState.value.copy(typoStrictness = strictness)
            }
        }
    }

    fun onEvent(event: TranslationGameEvent) {
        when (event) {
            is TranslationGameEvent.LoadCategories -> loadCategories()
            is TranslationGameEvent.SelectCategory -> {
                _uiState.value = _uiState.value.copy(selectedCategoryId = event.categoryId)
                if (_uiState.value.phase == LearningPhase.NEXT_PREP) cancelNextPrepTimer()
            }
            is TranslationGameEvent.SetWordCount -> {
                _uiState.value = _uiState.value.copy(wordCount = event.count)
            }
            is TranslationGameEvent.SetShuffled -> {
                _uiState.value = _uiState.value.copy(isShuffled = event.shuffled)
            }
            is TranslationGameEvent.SetTypoStrictness -> changeStrictness(event.strictness)
            is TranslationGameEvent.StartGame -> startGame()
            is TranslationGameEvent.UpdateInput -> {
                if (!_uiState.value.isAwaitingFeedback) {
                    _uiState.value = _uiState.value.copy(userInput = event.value)
                }
            }
            is TranslationGameEvent.SubmitAnswer -> submitAnswer()
            is TranslationGameEvent.ShowHintLetter -> showHintLetter()
            is TranslationGameEvent.RevealAnswer -> revealAnswer()
            is TranslationGameEvent.AdvanceCard -> advanceCard()
            is TranslationGameEvent.DismissXpPopup -> {
                _uiState.value = _uiState.value.copy(showXpPopup = false)
            }
            is TranslationGameEvent.ShowSettings -> {
                if (_uiState.value.phase == LearningPhase.NEXT_PREP) cancelNextPrepTimer()
                if (!_uiState.value.showSettingsSheet) {
                    _uiState.value = _uiState.value.copy(showSettingsSheet = true)
                }
            }
            is TranslationGameEvent.DismissSettings -> {
                if (_uiState.value.showSettingsSheet) {
                    _uiState.value = _uiState.value.copy(showSettingsSheet = false)
                }
            }
            is TranslationGameEvent.RequestExit -> {
                if (_uiState.value.phase == LearningPhase.GAME && _uiState.value.results.isNotEmpty()) {
                    _uiState.value = _uiState.value.copy(showExitConfirmation = true)
                }
            }
            is TranslationGameEvent.ConfirmExit -> {
                _uiState.value = _uiState.value.copy(showExitConfirmation = false)
            }
            is TranslationGameEvent.DismissExitDialog -> {
                _uiState.value = _uiState.value.copy(showExitConfirmation = false)
            }
            is TranslationGameEvent.EnterNextPrep -> enterNextPrep()
            is TranslationGameEvent.ConfirmNextSession -> {
                cancelNextPrepTimer()
                startGame()
            }
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
                val processed = if (_uiState.value.isShuffled) allWords.shuffled() else allWords
                val wordCount = _uiState.value.wordCount
                val selected = if (wordCount == 0) processed else processed.take(wordCount)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    words = selected,
                    currentIndex = 0,
                    userInput = "",
                    hintLettersShown = 0,
                    answerRevealed = false,
                    checkResult = null,
                    questionStartMillis = System.currentTimeMillis(),
                    results = emptyList(),
                    summaries = emptyList(),
                    totalXpEarned = 0,
                    progressDelta = null,
                    gradePreview = null,
                    phase = LearningPhase.GAME,
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

    private fun submitAnswer() {
        val state = _uiState.value
        val word = state.currentWord ?: return
        if (state.isAwaitingFeedback) return

        val similarity = StringSimilarity.normalizedSimilarity(state.userInput, word.translation)
        val timeMs = System.currentTimeMillis() - state.questionStartMillis
        val rating = mapInputToRating(
            similarity = similarity,
            hintLettersShown = state.hintLettersShown,
            answerRevealed = state.answerRevealed,
            timeMs = timeMs,
            strictness = state.typoStrictness
        )
        val isCorrect = rating != Rating.Again

        val result = TranslationCheckResult(
            isCorrect = isCorrect,
            similarity = similarity,
            correctAnswer = word.translation,
            rating = rating,
            timeMillis = timeMs
        )
        _uiState.value = _uiState.value.copy(checkResult = result)

        applyAnswer(word, rating, isCorrect)
        scheduleAutoAdvance()
    }

    private fun mapInputToRating(
        similarity: Float,
        hintLettersShown: Int,
        answerRevealed: Boolean,
        timeMs: Long,
        strictness: TypoStrictness
    ): Rating {
        if (answerRevealed) return Rating.Again

        val base = when {
            similarity < strictness.partialThreshold -> Rating.Again
            similarity < strictness.correctThreshold -> Rating.Hard
            else -> Rating.Good
        }

        // Penalty: a hint was used, cap at Hard.
        val capped = if (hintLettersShown > 0 && base == Rating.Good) Rating.Hard else base

        // Bonus: perfect match, no hints, fast answer → Easy.
        return if (
            capped == Rating.Good &&
            similarity >= 0.999f &&
            hintLettersShown == 0 &&
            timeMs < FAST_ANSWER_THRESHOLD_MILLIS
        ) Rating.Easy else capped
    }

    private fun applyAnswer(word: Word, rating: Rating, isCorrect: Boolean) {
        val wasNew = word.phase == CardPhase.Added.value
        val xpGained = XP_BY_RATING[rating] ?: 0
        recordResult(word, isCorrect = isCorrect, xp = xpGained)

        viewModelScope.launch {
            updateWordProgressUseCase(word, rating)
                .onSuccess { newWord ->
                    if (_uiState.value.currentWord?.id == word.id) {
                        val delta = WordProgressDelta.from(word, newWord, wasNew)
                        val summary = LearningWordSummary(
                            word = word,
                            isCorrect = isCorrect,
                            delta = delta
                        )
                        _uiState.value = _uiState.value.copy(
                            progressDelta = delta,
                            summaries = _uiState.value.summaries + summary
                        )
                    }
                    if (isCorrect) {
                        recordWordReviewUseCase()
                    }
                }
        }
    }

    private fun recordResult(word: Word, isCorrect: Boolean, xp: Int) {
        val state = _uiState.value
        val result = FlashcardResult(wordId = word.id, isCorrect = isCorrect)
        _uiState.value = state.copy(
            results = state.results + result,
            totalXpEarned = state.totalXpEarned + xp,
            showXpPopup = xp > 0,
            xpPopupAmount = xp
        )
    }

    private fun showHintLetter() {
        val state = _uiState.value
        val word = state.currentWord ?: return
        if (state.isAwaitingFeedback) return
        val maxHint = (word.translation.length / 2).coerceAtLeast(1)
        val newHintCount = (state.hintLettersShown + 1).coerceAtMost(maxHint)
        // Auto-fill that many leading characters into the input if empty or shorter.
        val prefix = word.translation.take(newHintCount)
        val newInput = if (state.userInput.length < newHintCount) prefix else state.userInput
        _uiState.value = state.copy(
            hintLettersShown = newHintCount,
            userInput = newInput
        )
    }

    private fun revealAnswer() {
        val state = _uiState.value
        val word = state.currentWord ?: return
        if (state.isAwaitingFeedback) return
        _uiState.value = state.copy(
            userInput = word.translation,
            answerRevealed = true
        )
        // Don't submit automatically — user still presses "Готово" so they read the answer.
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
            delay(FEEDBACK_VISIBLE_MILLIS)
            advanceCard()
        }
    }

    private fun advanceCard() {
        advanceJob?.cancel()
        val state = _uiState.value
        if (state.phase != LearningPhase.GAME) return

        if (state.isLastCard) {
            _uiState.value = state.copy(phase = LearningPhase.SUMMARY)
        } else {
            val nextIdx = state.currentIndex + 1
            _uiState.value = state.copy(
                currentIndex = nextIdx,
                userInput = "",
                hintLettersShown = 0,
                answerRevealed = false,
                checkResult = null,
                questionStartMillis = System.currentTimeMillis(),
                progressDelta = null,
                gradePreview = null
            )
            computeGradePreview()
        }
    }

    private fun changeStrictness(strictness: TypoStrictness) {
        if (_uiState.value.typoStrictness == strictness) return
        _uiState.value = _uiState.value.copy(typoStrictness = strictness)
        viewModelScope.launch { settingsRepository.setLastTranslationStrictness(strictness.name) }
    }

    private fun enterNextPrep() {
        advanceJob?.cancel()
        cancelNextPrepTimer()
        _uiState.value = _uiState.value.copy(phase = LearningPhase.NEXT_PREP)
        if (_uiState.value.selectedCategoryHasWords) startNextPrepTimer()
    }

    private fun startNextPrepTimer() {
        nextPrepJob?.cancel()
        _uiState.value = _uiState.value.copy(nextPrepCountdownSeconds = NEXT_PREP_TOTAL_SECONDS)
        nextPrepJob = viewModelScope.launch {
            for (s in NEXT_PREP_TOTAL_SECONDS downTo 1) {
                _uiState.value = _uiState.value.copy(nextPrepCountdownSeconds = s)
                delay(1000)
            }
            if (_uiState.value.phase == LearningPhase.NEXT_PREP &&
                _uiState.value.selectedCategoryHasWords
            ) {
                cancelNextPrepTimer()
                startGame()
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

    fun handleBackPress(): Boolean {
        return if (_uiState.value.phase == LearningPhase.GAME && _uiState.value.results.isNotEmpty()) {
            _uiState.value = _uiState.value.copy(showExitConfirmation = true)
            true
        } else {
            false
        }
    }
}
