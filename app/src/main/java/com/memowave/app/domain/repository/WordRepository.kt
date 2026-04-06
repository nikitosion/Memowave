package com.memowave.app.domain.repository

import com.memowave.app.domain.model.Word

interface WordRepository {
    suspend fun getWords(): Result<List<Word>>
    suspend fun getWord(id: Long): Result<Word?>
    suspend fun addWord(word: Word): Result<Word>
    suspend fun updateWord(word: Word): Result<Word>
    suspend fun deleteWord(id: Long): Result<Unit>
}