package com.notisiren.shared.domain.usecase

import com.notisiren.core.extensions.containsIgnoreCase
import com.notisiren.shared.domain.model.GmailFilter
import com.notisiren.shared.domain.model.GmailNotification
import jakarta.inject.Inject

class CheckGmailFilter @Inject constructor() {

    operator fun invoke(
        notification: GmailNotification,
        filters: List<GmailFilter>
    ): GmailFilter? = filters.firstOrNull { it.isEnabled && it.matches(notification)}

    private fun GmailFilter.matches(notification: GmailNotification): Boolean {
        val senderOk = senderContains.isNullOrBlank() ||
                notification.sender.containsIgnoreCase(senderContains)
        val subjectOk = subjectContains.isNullOrBlank() ||
                notification.subject.containsIgnoreCase(subjectContains)
        return senderOk && subjectOk
    }

}