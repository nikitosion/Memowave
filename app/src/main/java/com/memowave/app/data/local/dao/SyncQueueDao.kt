package com.memowave.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.memowave.app.data.local.entity.SyncQueueEntity

@Dao
interface SyncQueueDao {

    @Query("SELECT * FROM sync_queue")
    suspend fun getAllSyncItems(): List<SyncQueueEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSyncItem(syncItem: SyncQueueEntity) : Long

    @Delete
    suspend fun deleteSyncItem(syncItem: SyncQueueEntity)

    @Query("DELETE FROM sync_queue WHERE id = :id")
    suspend fun deleteSyncItemById(id: Long)
}