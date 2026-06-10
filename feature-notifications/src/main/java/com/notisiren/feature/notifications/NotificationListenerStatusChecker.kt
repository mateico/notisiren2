package com.notisiren.feature.notifications

import android.content.Context
import android.provider.Settings
import com.notisiren.core.constants.AppConstants
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationListenerStatusChecker @Inject constructor() {

    fun isNotificationListenerEnabled(context: Context): Boolean {

        return try {
            val enabledListener = Settings.Secure.getString(
                context. contentResolver,
                "enabled_notification_listeners"
            ) ?: return false

            val serviceName =
                "${AppConstants.APP_PACKAGE_NAME}/${GmailNotificationListener::class.java.canonicalName}"
            enabledListener.contains(serviceName)
        } catch (e: Exception) {
            false
        }

    }

}