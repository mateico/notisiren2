package com.notisiren.shared.domain.usecase

import com.notisiren.shared.domain.model.GmailFilter
import com.notisiren.shared.domain.repository.FilterRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetAllFilters @Inject constructor(
    private val repository: FilterRepository
) {

    operator fun invoke(): Flow<List<GmailFilter>> = repository.getAllFilters()

}