package com.gdn.android.onestop.group.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import com.gdn.android.onestop.base.Constant
import com.gdn.android.onestop.group.R

class MeetingAlarmPublisher : BroadcastReceiver() {

  override fun onReceive(context: Context?, intent: Intent?) {
    val notificationManager =
      context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val notification: Notification = intent!!.getParcelableExtra(Constant.NOTIF_MEETING_CHANNEL_ID)!!
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val notificationChannel = NotificationChannel(
        Constant.NOTIF_MEETING_CHANNEL_ID, "Meeting Notification", NotificationManager.IMPORTANCE_HIGH
      )

      notificationManager.createNotificationChannel(notificationChannel)
    }
    val id = Constant.NOTIF_MEETING_ID
    notificationManager.notify(id, notification)
  }

}