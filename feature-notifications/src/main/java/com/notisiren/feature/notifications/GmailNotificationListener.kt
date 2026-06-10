package com.notisiren.feature.notifications

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.core.app.NotificationCompat
import com.notisiren.core.constants.AppConstants
import com.notisiren.shared.domain.model.GmailNotification
import com.notisiren.shared.domain.usecase.CheckGmailFilter
import com.notisiren.shared.domain.usecase.GetAllFilters
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class GmailNotificationListener : NotificationListenerService() {

    @Inject
    lateinit var checkGmailFilter: CheckGmailFilter

    @Inject
    lateinit var alarmPlayer: AlarmPlayer

    @Inject
    lateinit var getAllFilters: GetAllFilters

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)

        if(sbn.packageName != AppConstants.GMAIL_PACKAGE_NAME) return

        val notification = extractGmailNotification(sbn) ?: return

        scope.launch {
            try {
                val filters = getAllFilters().first()
                val matchingFilter = checkGmailFilter(notification, filters)

                if (matchingFilter != null) {
                    alarmPlayer.playAlarm(this@GmailNotificationListener)
                }
            } catch (e: Exception) {
                // Log error or silently fail (notification listener shouldn't crash)
            }
        }
    }

    private fun extractGmailNotification(sbn: StatusBarNotification): GmailNotification? {
        val notification = sbn.notification
        val extras = notification.extras

        val sender = extras.getCharSequence(NotificationCompat.EXTRA_SUMMARY_TEXT)?.toString()
        val subject = extras.getCharSequence(NotificationCompat.EXTRA_TITLE)?.toString()

        if(sender.isNullOrEmpty() && subject.isNullOrEmpty()) return null

        return GmailNotification(sender = sender, subject = subject)
    }
}