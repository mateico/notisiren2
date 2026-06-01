package com.notisiren.shared.domain.usecase

import com.notisiren.shared.domain.model.GmailFilter
import com.notisiren.shared.domain.repository.FilterRepository
import javax.inject.Inject

class CreateFilter @Inject constructor(
    private val repository: FilterRepository
) {

    suspend operator fun invoke(filter: GmailFilter): Result<Long> {
        if (filter.name.isBlank()) {
            return Result.failure(IllegalArgumentException("Filter name must not be blank"))
        }

        if (filter.senderContains.isNullOrBlank() && filter.subjectContains.isNullOrBlank()) {
            return Result.failure(
                IllegalArgumentException("A filter needs a sender or a subject to match on")
            )
        }

        return Result.success(repository.upsertFilter(filter))
    }

}