package com.gdn.android.onestop.library.util

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.gdn.android.onestop.base.Constant
import com.gdn.android.onestop.library.R
import com.gdn.android.onestop.library.data.Audio
import com.gdn.android.onestop.library.data.LibraryDao
import com.gdn.android.onestop.library.injection.LibraryComponent
import com.gdn.android.onestop.library.viewmodel.AudioCatalogViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class AudioDownloadService : Service() {

  private lateinit var audio: Audio

  @Inject
  lateinit var viewModel: AudioCatalogViewModel

  @Inject
  lateinit var libraryDao: LibraryDao

  private val maxProgress = 100

  override fun onBind(intent: Intent?): IBinder? {
    return null
  }

  override fun onCreate() {
    LibraryComponent.getInstance().inject(this)
    super.onCreate()
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    val randomId = Math.random().toInt()
    audio = intent!!.getSerializableExtra("audio") as Audio
    val notification = NotificationCompat.Builder(applicationContext,
        Constant.NOTIF_DOWNLOAD_CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_default_user).setContentTitle("Downloading audio ${audio.title}")
        .setContentText("Download in progress")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setOngoing(true)
        .setOnlyAlertOnce(true)
        .setProgress(maxProgress, 0, false)

    val notificationManager = NotificationManagerCompat.from(this.applicationContext)
    notificationManager.notify(randomId, notification.build())

    viewModel.downloadAudio(audio).subscribe({
      if (it == maxProgress) {
        Log.d("audio", "update percentage complete $it")
        notification
            .setContentText("Download finished")
            .setProgress(0, 0, false).setOngoing(false)
        notificationManager.notify(randomId, notification.build())

        viewModel.launch {
          audio.isDownloaded = true
          libraryDao.insertAudio(audio)
        }
      } else {
        notification.setProgress(100, it, false)
        notificationManager.notify(randomId, notification.build())
        Log.d("audio", "update percentage $it")
      }

    }, {
      notification
          .setContentText("Download failed")
          .setProgress(0, 0, false).setOngoing(false)
      notificationManager.notify(randomId, notification.build())
    })

    return START_STICKY
  }


}