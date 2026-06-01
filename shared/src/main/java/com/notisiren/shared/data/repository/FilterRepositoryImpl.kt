package com.notisiren.shared.data.repository

import com.notisiren.shared.data.local.dao.FilterDao
import com.notisiren.shared.data.mapper.toDomain
import com.notisiren.shared.data.mapper.toEntity
import com.notisiren.shared.domain.model.GmailFilter
import com.notisiren.shared.domain.repository.FilterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FilterRepositoryImpl @Inject constructor(
    private val dao: FilterDao
): FilterRepository {
    override fun getAllFilters(): Flow<List<GmailFilter>> =
        dao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getFilterById(id: Long): GmailFilter? =
        dao.getById(id)?.toDomain()

    override suspend fun upsertFilter(filter: GmailFilter): Long =
        dao.upsert(filter.toEntity())

    override suspend fun deleteFilter(filter: GmailFilter) =
        dao.delete(filter.toEntity())
}