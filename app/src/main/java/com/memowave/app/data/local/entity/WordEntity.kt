package com.memowave.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    tableName = "words",
    indices = [
        Index(value = ["original"]),
        Index(value = ["categoryId"])
    ]
)
data class WordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val original: String,
    val translation: String,
    val categoryId: Long? = null,
    val example: List<String> = emptyList(),
    val imageUrl: String? = null,
    val note: String? = null,
    val isFavorite: Boolean = false,
    val isSynced: Boolean = false,
    val remoteId: Long? = null,
    val createdAt: Long,
    val updatedAt: Long,
    // SRS fields
    val stability: Double = 2.5,
    val difficulty: Double = 2.5,
    val interval: Int = 0,
    val dueDate: LocalDateTime = LocalDateTime.now(),
    val reviewCount: Int = 0,
    val lastReview: LocalDateTime? = null,
    val phase: Int = 0,
)