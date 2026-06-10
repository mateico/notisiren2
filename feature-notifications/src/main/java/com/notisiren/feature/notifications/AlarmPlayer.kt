package com.notisiren.feature.notifications

import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.content.getSystemService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmPlayer @Inject constructor() {

    fun playAlarm(context: Context) {
        // Play default alarm sound
        val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        val ringtone = RingtoneManager.getRingtone(context, alarmUri)
        ringtone.play()

        // Vibrate
        val vibrator = context.getSystemService<Vibrator>()
        if(vibrator != null && vibrator.hasVibrator()) {
            val pattern = longArrayOf(0, 500, 200, 500, 200, 500) // OFF, ON, OFF, ON, OFF, ON
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = VibrationEffect.createWaveform(pattern, -1) // -1 = no repeat
                vibrator.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(pattern, -1)
            }
        }
    }
}