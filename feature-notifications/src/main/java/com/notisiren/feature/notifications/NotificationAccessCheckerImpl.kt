package com.notisiren.feature.notifications

import android.content.ComponentName
import android.content.Context
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import com.notisiren.shared.domain.repository.NotificationAccessChecker
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class NotificationAccessCheckerImpl @Inject constructor(
    @ApplicationContext private val appContext: Context
): NotificationAccessChecker {
    override fun isEnabled(): Flow<Boolean> = callbackFlow {
        trySend(checkAccess())

        val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                trySend(checkAccess())
            }
        }

        val uri = Settings.Secure.getUriFor("enabled_notification_listeners")
        appContext.contentResolver.registerContentObserver(uri, false, observer)

        awaitClose {
            appContext.contentResolver.unregisterContentObserver(observer)
        }
    }

    private fun checkAccess(): Boolean {
        val enabledListener = Settings.Secure.getString(
            appContext.contentResolver,
            "enabled_notification_listeners"
        ) ?: return false

        val myComponent = ComponentName(appContext, GmailNotificationListener::class.java)
        return enabledListener.split(":")
            .mapNotNull { ComponentName.unflattenFromString(it) }
            .any { it.packageName == myComponent.packageName && it.className == myComponent.className}
    }
}