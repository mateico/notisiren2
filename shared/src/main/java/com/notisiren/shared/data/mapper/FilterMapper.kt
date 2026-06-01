package com.notisiren.shared.data.mapper

import com.notisiren.shared.data.local.entity.FilterEntity
import com.notisiren.shared.domain.model.GmailFilter

fun FilterEntity.toDomain(): GmailFilter = GmailFilter(
    this.id,
    this.name,
    this.senderContains,
    this.subjectContains,
    this.isEnabled
)


fun GmailFilter.toEntity(): FilterEntity = FilterEntity(
    this.id,
    this.name,
    this.senderContains,
    this.subjectContains,
    this.isEnabled
)
