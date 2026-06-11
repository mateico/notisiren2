package com.notisiren.shared.domain.repository

import kotlinx.coroutines.flow.Flow

interface NotificationAccessChecker {
    fun isEnabled(): Flow<Boolean>
}