package com.gdn.android.onestop.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import com.gdn.android.onestop.base.BaseComponent
import com.gdn.android.onestop.base.Constant
import com.gdn.android.onestop.base.DaggerBaseComponent
import com.gdn.android.onestop.base.util.SessionManager

class OneStopApplication : Application(){

    val appComponent : BaseComponent by lazy { DaggerBaseComponent.factory()
        .create(this, SessionManager(this), baseContext) }

    override fun onCreate() {
        appComponent.inject(this)
        BaseComponent.setInstance(appComponent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = NotificationManagerCompat.from(this)

            val downloadNotifChannel = NotificationChannel(
                Constant.NOTIF_DOWNLOAD_CHANNEL_ID, Constant.NOTIF_DOWNLOAD_CHANNEL_ID,
                NotificationManager.IMPORTANCE_HIGH)

            notificationManager.createNotificationChannel(downloadNotifChannel)

            val chatNotifChannel = NotificationChannel(
                Constant.NOTIF_CHAT_CHANNEL_ID, Constant.NOTIF_CHAT_CHANNEL_ID,
                NotificationManager.IMPORTANCE_HIGH)

            notificationManager.createNotificationChannel(chatNotifChannel)
        }
        super.onCreate()
    }
}