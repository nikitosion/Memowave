package com.memowave.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.memowave.app.data.local.entity.WordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {

    @Query("SELECT * FROM words ORDER BY createdAt DESC")
    fun observeWords(): Flow<List<WordEntity>>

    @Query("SELECT * FROM words ORDER BY createdAt DESC")
    suspend fun getWords(): List<WordEntity>

    @Query("SELECT * FROM words WHERE id = :id LIMIT 1")
    suspend fun getWordById(id: Long): WordEntity?

    @Query("SELECT * FROM words WHERE categoryId = :categoryId ORDER BY createdAt DESC")
    suspend fun getWordsByCategory(categoryId: Long): List<WordEntity>

    @Query(
        """
        SELECT * FROM words 
        WHERE original LIKE '%' || :query || '%' 
           OR translation LIKE '%' || :query || '%'
        ORDER BY createdAt DESC
        """
    )
    suspend fun searchWords(query: String): List<WordEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(word: WordEntity): Long

    @Update
    suspend fun updateWord(word: WordEntity)

    @Delete
    suspend fun deleteWord(word: WordEntity)

    @Query("DELETE FROM words WHERE id = :id")
    suspend fun deleteWordById(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(words: List<WordEntity>)

    @Query("SELECT * FROM words WHERE remoteId = :remoteId LIMIT 1")
    suspend fun getWordByRemoteId(remoteId: Long): WordEntity?

    @Transaction
    suspend fun upsertWordsByRemoteId(words: List<WordEntity>) {
        for (word in words) {
            val existing = getWordByRemoteId(word.remoteId!!)
            if (existing != null) {
                if (existing.isSynced) {
                    updateWord(
                        word.copy(
                            id = existing.id,
                            isSynced = true,
                            // server doesn't know about SRS fields yet — preserve local progress
                            stability = existing.stability,
                            difficulty = existing.difficulty,
                            interval = existing.interval,
                            dueDate = existing.dueDate,
                            reviewCount = existing.reviewCount,
                            lastReview = existing.lastReview,
                            phase = existing.phase,
                        )
                    )
                }
            } else {
                insertWord(word)
            }
        }
    }

    @Query("DELETE FROM words WHERE isSynced = 1 AND remoteId NOT IN (:ids)")
    suspend fun deleteSyncedWordsNotInIds(ids: List<Long>)

    @Query("DELETE FROM words")
    suspend fun deleteAll()
}