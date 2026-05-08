package com.memowave.app.ui.screen.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memowave.app.domain.algorithm.CardPhase
import com.memowave.app.domain.model.FlashcardResult
import com.memowave.app.domain.model.Rating
import com.memowave.app.domain.model.Word
import com.memowave.app.domain.repository.SettingsRepository
import com.memowave.app.domain.usecase.category.GetCategoriesUseCase
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

private const val TICK_INTERVAL_MILLIS = 50L
private const val FEEDBACK_FLASH_MILLIS = 450L
private const val NEXT_PREP_TOTAL_SECONDS = 5
private const val FAST_ANSWER_THRESHOLD_MILLIS = 1500L
private const val MEDIUM_ANSWER_THRESHOLD_MILLIS = 6000L
private const val FAST_STREAK_FOR_EASY = 3

private val XP_BY_RATING = mapOf(
    Rating.Again to 0,
    Rating.Hard to 15,
    Rating.Good to 25,
    Rating.Easy to 35
)

@HiltViewModel
class QuizGameViewModel @Inject constructor(
    private val getWordsUseCase: GetWordsUseCase,
    private val getWordsByCategoryUseCase: GetWordsByCategoryUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val updateWordProgressUseCase: UpdateWordProgressUseCase,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizGameUiState())
    val uiState: StateFlow<QuizGameUiState> = _uiState.asStateFlow()

    private var sessionTimerJob: Job? = null
    private var advanceJob: Job? = null
    private var nextPrepJob: Job? = null

    init {
        viewModelScope.launch {
            val saved = settingsRepository.getSettings().first().lastQuizDurationSeconds
            val seconds = saved.coerceIn(15, 600)
            _uiState.value = _uiState.value.copy(
                totalDurationSeconds = seconds,
                remainingMillis = seconds * 1000L
            )
        }
    }

    fun onEvent(event: QuizGameEvent) {
        when (event) {
            is QuizGameEvent.LoadCategories -> loadCategories()
            is QuizGameEvent.SelectCategory -> {
                _uiState.value = _uiState.value.copy(selectedCategoryId = event.categoryId)
                if (_uiState.value.phase == LearningPhase.NEXT_PREP) cancelNextPrepTimer()
            }
            is QuizGameEvent.SetShuffled -> {
                _uiState.value = _uiState.value.copy(isShuffled = event.shuffled)
            }
            is QuizGameEvent.SetDurationSeconds -> changeDuration(event.seconds)
            is QuizGameEvent.StartGame -> startGame()
            is QuizGameEvent.SelectOption -> selectOption(event.index)
            is QuizGameEvent.DismissXpPopup -> {
                _uiState.value = _uiState.value.copy(showXpPopup = false)
            }
            is QuizGameEvent.ShowSettings -> {
                if (_uiState.value.phase == LearningPhase.NEXT_PREP) cancelNextPrepTimer()
                if (!_uiState.value.showSettingsSheet) {
                    _uiState.value = _uiState.value.copy(showSettingsSheet = true)
                }
            }
            is QuizGameEvent.DismissSettings -> {
                if (_uiState.value.showSettingsSheet) {
                    _uiState.value = _uiState.value.copy(showSettingsSheet = false)
                }
            }
            is QuizGameEvent.RequestExit -> {
                if (_uiState.value.phase == LearningPhase.GAME && _uiState.value.results.isNotEmpty()) {
                    _uiState.value = _uiState.value.copy(showExitConfirmation = true)
                    pauseSessionTimer()
                }
            }
            is QuizGameEvent.ConfirmExit -> {
                _uiState.value = _uiState.value.copy(showExitConfirmation = false)
                stopSessionTimer()
            }
            is QuizGameEvent.DismissExitDialog -> {
                _uiState.value = _uiState.value.copy(showExitConfirmation = false)
                if (_uiState.value.phase == LearningPhase.GAME) startSessionTimer()
            }
            is QuizGameEvent.EnterNextPrep -> enterNextPrep()
            is QuizGameEvent.ConfirmNextSession -> {
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
                if (allWords.size < 4) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Нужно минимум 4 слова для викторины"
                    )
                    return@onSuccess
                }
                val pool = if (_uiState.value.isShuffled) allWords.shuffled() else allWords
                val durationMs = _uiState.value.totalDurationSeconds * 1000L
                val (firstWord, options, correctIdx) = nextQuestion(pool, previousWordId = null)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    wordPool = pool,
                    currentWord = firstWord,
                    options = options,
                    correctOptionIndex = correctIdx,
                    selectedOptionIndex = null,
                    questionStartMillis = System.currentTimeMillis(),
                    streak = 0,
                    results = emptyList(),
                    summaries = emptyList(),
                    totalXpEarned = 0,
                    progressDelta = null,
                    phase = LearningPhase.GAME,
                    remainingMillis = durationMs,
                    nextPrepCountdownSeconds = null
                )
                startSessionTimer()
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "Ошибка загрузки слов"
                )
            }
        }
    }

    private fun nextQuestion(
        pool: List<Word>,
        previousWordId: Long?
    ): Triple<Word, List<String>, Int> {
        val candidates = pool.filter { it.id != previousWordId }
        val word = candidates.randomOrNull() ?: pool.random()
        val distractors = pool
            .filter { it.id != word.id && it.translation != word.translation }
            .shuffled()
            .take(3)
            .map { it.translation }
        val padded = if (distractors.size < 3) {
            distractors + List(3 - distractors.size) { "—" }
        } else distractors
        val correctIdx = (0..3).random()
        val options = padded.toMutableList().apply { add(correctIdx, word.translation) }
        return Triple(word, options, correctIdx)
    }

    private fun selectOption(index: Int) {
        val state = _uiState.value
        if (state.selectedOptionIndex != null) return
        val word = state.currentWord ?: return

        val isCorrect = index == state.correctOptionIndex
        val timeMs = System.currentTimeMillis() - state.questionStartMillis
        val newStreak = if (isCorrect) state.streak + 1 else 0
        val rating = mapAnswerToRating(isCorrect, timeMs, newStreak)

        _uiState.value = state.copy(
            selectedOptionIndex = index,
            streak = newStreak
        )

        applyAnswer(word, rating, isCorrect)

        // Brief flash, then next question
        advanceJob?.cancel()
        advanceJob = viewModelScope.launch {
            delay(FEEDBACK_FLASH_MILLIS)
            advanceToNextQuestion()
        }
    }

    private fun mapAnswerToRating(isCorrect: Boolean, timeMs: Long, streak: Int): Rating {
        if (!isCorrect) return Rating.Again
        return when {
            timeMs < FAST_ANSWER_THRESHOLD_MILLIS && streak >= FAST_STREAK_FOR_EASY -> Rating.Easy
            timeMs > MEDIUM_ANSWER_THRESHOLD_MILLIS -> Rating.Hard
            else -> Rating.Good
        }
    }

    private fun applyAnswer(word: Word, rating: Rating, isCorrect: Boolean) {
        val wasNew = word.phase == CardPhase.Added.value
        val xpGained = XP_BY_RATING[rating] ?: 0
        recordResult(word, isCorrect = isCorrect, xp = xpGained)

        viewModelScope.launch {
            updateWordProgressUseCase(word, rating)
                .onSuccess { newWord ->
                    val delta = WordProgressDelta.from(word, newWord, wasNew)
                    val summary = LearningWordSummary(
                        word = word,
                        isCorrect = isCorrect,
                        delta = delta
                    )
                    // Append summary; only update displayed delta if this answer is the most recent.
                    _uiState.value = _uiState.value.copy(
                        progressDelta = delta,
                        summaries = _uiState.value.summaries + summary
                    )
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

    private fun advanceToNextQuestion() {
        val state = _uiState.value
        if (state.phase != LearningPhase.GAME) return
        if (state.remainingMillis <= 0) return
        val (nextWord, options, correctIdx) = nextQuestion(state.wordPool, state.currentWord?.id)
        _uiState.value = state.copy(
            currentWord = nextWord,
            options = options,
            correctOptionIndex = correctIdx,
            selectedOptionIndex = null,
            questionStartMillis = System.currentTimeMillis(),
            progressDelta = null
        )
    }

    private fun startSessionTimer() {
        sessionTimerJob?.cancel()
        sessionTimerJob = viewModelScope.launch {
            while (_uiState.value.phase == LearningPhase.GAME && _uiState.value.remainingMillis > 0) {
                delay(TICK_INTERVAL_MILLIS)
                val state = _uiState.value
                if (state.showExitConfirmation) continue
                val newRemaining = (state.remainingMillis - TICK_INTERVAL_MILLIS).coerceAtLeast(0L)
                _uiState.value = state.copy(remainingMillis = newRemaining)
                if (newRemaining <= 0) finishSession()
            }
        }
    }

    private fun pauseSessionTimer() {
        sessionTimerJob?.cancel()
    }

    private fun stopSessionTimer() {
        sessionTimerJob?.cancel()
    }

    private fun finishSession() {
        sessionTimerJob?.cancel()
        advanceJob?.cancel()
        _uiState.value = _uiState.value.copy(
            phase = LearningPhase.SUMMARY,
            remainingMillis = 0L
        )
    }

    private fun changeDuration(seconds: Int) {
        val clamped = seconds.coerceIn(15, 600)
        if (_uiState.value.totalDurationSeconds == clamped) return
        _uiState.value = _uiState.value.copy(
            totalDurationSeconds = clamped,
            // If we're in lobby/next_prep, also update the displayed remaining.
            remainingMillis = if (_uiState.value.phase == LearningPhase.GAME) {
                _uiState.value.remainingMillis
            } else clamped * 1000L
        )
        viewModelScope.launch { settingsRepository.setLastQuizDurationSeconds(clamped) }
    }

    private fun enterNextPrep() {
        sessionTimerJob?.cancel()
        advanceJob?.cancel()
        cancelNextPrepTimer()
        _uiState.value = _uiState.value.copy(
            phase = LearningPhase.NEXT_PREP,
            remainingMillis = _uiState.value.totalDurationSeconds * 1000L
        )
        if (_uiState.value.canStart) startNextPrepTimer()
    }

    private fun startNextPrepTimer() {
        nextPrepJob?.cancel()
        _uiState.value = _uiState.value.copy(nextPrepCountdownSeconds = NEXT_PREP_TOTAL_SECONDS)
        nextPrepJob = viewModelScope.launch {
            for (s in NEXT_PREP_TOTAL_SECONDS downTo 1) {
                _uiState.value = _uiState.value.copy(nextPrepCountdownSeconds = s)
                delay(1000)
            }
            if (_uiState.value.phase == LearningPhase.NEXT_PREP && _uiState.value.canStart) {
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
            pauseSessionTimer()
            true
        } else {
            false
        }
    }
}
