package com.notisiren.shared.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "filters")
data class FilterEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val senderContains: String?,
    val subjectContains: String?,
    val isEnabled: Boolean
)