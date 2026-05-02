package com.memowave.app.domain.model

enum class Rating(val value: Int) {
    Again(1),
    Hard(2),
    Good(3),
    Easy(4)
}

data class WordGrade (
    val title: String,
    val durationMillis: Long = 0,
    val interval: Int = 0,
    val text: String = "0",
    val rating: Rating,
    val stability: Double = 0.0,
    val difficulty: Double = 0.0
)