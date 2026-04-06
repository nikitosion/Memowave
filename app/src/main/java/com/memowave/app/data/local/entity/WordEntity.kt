package com.memowave.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

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
    val note: String? = null,
    val isFavorite: Boolean = false,
    val createdAt: Long,
    val updatedAt: Long
)