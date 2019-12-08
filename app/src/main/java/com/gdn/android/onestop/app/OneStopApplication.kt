package com.gdn.android.onestop.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import com.gdn.android.onestop.base.AppComponent
import com.gdn.android.onestop.base.Constant
import com.gdn.android.onestop.base.DaggerAppComponent
import com.gdn.android.onestop.base.util.SessionManager

class OneStopApplication : Application(){

    val mAppComponent : AppComponent by lazy { DaggerAppComponent.factory()
        .create(this, SessionManager(this), baseContext) }

    override fun onCreate() {
        mAppComponent.inject(this)
        AppComponent.setInstance(mAppComponent)
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