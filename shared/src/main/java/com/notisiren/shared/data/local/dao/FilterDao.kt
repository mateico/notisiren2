package com.notisiren.shared.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.notisiren.shared.data.local.entity.FilterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FilterDao {

    @Query("SELECT * FROM filters ORDER BY id DESC")
    fun observeAll(): Flow<List<FilterEntity>>

    @Query("SELECT * FROM filters WHERE id = :id")
    suspend fun getById(id: Long): FilterEntity?

    @Upsert
    suspend fun upsert(filter: FilterEntity): Long

    @Delete
    suspend fun delete(filter: FilterEntity)
}