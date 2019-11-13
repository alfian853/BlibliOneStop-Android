package com.gdn.android.onestop.library.util

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.viewModelScope
import com.gdn.android.onestop.library.R
import com.gdn.android.onestop.library.data.Book
import com.gdn.android.onestop.library.data.LibraryDao
import com.gdn.android.onestop.library.injection.LibraryComponent
import com.gdn.android.onestop.library.viewmodel.BookCatalogViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class BookDownloadService : Service() {

  private lateinit var book: Book

  @Inject
  lateinit var viewModel: BookCatalogViewModel

  @Inject
  lateinit var libraryDao: LibraryDao

  private val maxProgress = 100

  override fun onBind(intent: Intent?): IBinder? {
    return null
  }

  private val downloadNotifId = 5

  override fun onCreate() {
    LibraryComponent.getInstance().inject(this)
    super.onCreate()
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    book = intent!!.getSerializableExtra("book") as Book
    val notification = NotificationCompat.Builder(applicationContext,
        Constant.NOTIF_DOWNLOAD_ID)
        .setSmallIcon(R.drawable.ic_default_user).setContentTitle("Downloading book ${book.title}")
        .setContentText("Download in progress")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setOngoing(true)
        .setOnlyAlertOnce(true)
        .setProgress(maxProgress, 0, false)

    val notificationManager = NotificationManagerCompat.from(this.applicationContext)
    notificationManager.notify(downloadNotifId, notification.build())

    viewModel.downloadBook(book).subscribe({
      if (it == maxProgress) {
        Log.d("book", "update percentage complete $it")
        notification
            .setContentText("Download finished")
            .setProgress(0, 0, false).setOngoing(false)
        notificationManager.notify(downloadNotifId, notification.build())

        viewModel.viewModelScope.launch {
          book.isDownloaded = true
          libraryDao.insertBook(book)
        }
      } else {
        notification.setProgress(100, it, false)
        notificationManager.notify(downloadNotifId, notification.build())
        Log.d("book", "update percentage $it")
      }

    }, {
      notification
          .setContentText("Download failed")
          .setProgress(0, 0, false).setOngoing(false)
      notificationManager.notify(downloadNotifId, notification.build())
    })

    return START_STICKY
  }


}