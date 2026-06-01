package com.notisiren.shared.domain.repository

import com.notisiren.shared.domain.model.GmailFilter
import kotlinx.coroutines.flow.Flow

interface FilterRepository {

    fun getAllFilters(): Flow<List<GmailFilter>>

    suspend fun getFilterById(id: Long): GmailFilter?

    suspend fun upsertFilter(filter: GmailFilter): Long

    suspend fun deleteFilter(filter: GmailFilter)
}