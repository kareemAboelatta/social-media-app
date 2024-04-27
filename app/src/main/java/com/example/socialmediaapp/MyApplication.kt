package com.example.socialmediaapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {
    private lateinit var notificationManager: NotificationManager

    companion object {
        const val CHANNEL_ID = "SocialMediaPlus"
        const val CHANNEL_NAME = "SocialMediaPlus"
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        notificationManager =
            base.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        createAppChannel()
    }

    private fun createAppChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )

            val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            notificationChannel.setSound(
                defaultSound,
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )

            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}