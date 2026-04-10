package com.memowave.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.memowave.app.data.sync.SyncEntityType
import com.memowave.app.data.sync.SyncOperationType

@Entity(tableName = "sync_queue")
data class SyncQueueEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val entityType: SyncEntityType,
    val entityId: Long,
    val operationType: SyncOperationType,
    val remoteId: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
)
