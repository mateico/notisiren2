package com.notisiren.shared.domain.model

data class GmailFilter(
    val id: Long = 0,
    val name: String,
    val senderContains: String? = null,
    val subjectContains: String? = null,
    val isEnabled: Boolean = true
)