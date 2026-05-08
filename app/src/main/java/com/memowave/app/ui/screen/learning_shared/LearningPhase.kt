package com.memowave.app.ui.screen.learning_shared

/**
 * Common phase model shared by every learning game (Flashcard, Translation, Quiz, ...).
 * Each mode walks through the same four phases:
 *   - LOBBY: pick category and settings, start session
 *   - GAME: active session, mode-specific UI
 *   - SUMMARY: results breakdown after the session ends
 *   - NEXT_PREP: between-sessions prep with category re-selection and a 5s auto-start timer
 */
enum class LearningPhase {
    LOBBY,
    GAME,
    SUMMARY,
    NEXT_PREP
}

enum class LoadState {
    IDLE,
    LOADING,
    LOADED,
    ERROR
}
