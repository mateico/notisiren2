package com.notisiren.feature.notifications.di

import com.notisiren.feature.notifications.NotificationAccessCheckerImpl
import com.notisiren.shared.domain.repository.NotificationAccessChecker
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class NotificationAccessModule {
    @Binds
    abstract fun bindNotificationAccessChecker(
        impl: NotificationAccessCheckerImpl
    ): NotificationAccessChecker
}