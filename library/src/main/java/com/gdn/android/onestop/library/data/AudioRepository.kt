package com.gdn.android.onestop.library.data

import android.content.Context
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.FileOutputStream
import java.io.OutputStream

class AudioRepository(
  private val context: Context,
  private val libraryDao: LibraryDao,
  private val libraryClient: LibraryClient,
  private val AudioUpdateManager: AudioUpdateManager
) {
  fun getLibraryLiveData() = libraryDao.getAudiosLiveData()

  suspend fun doFetchLatestData() {

    val response = libraryClient.getAudios(AudioUpdateManager.getLastUpdate())

    if (response.isSuccessful) {
      val Audios = response.body()!!.data!!
      if (Audios.isNotEmpty()) {
        libraryDao.insertAudio(Audios)
        val lastUpdate = Audios.maxBy { it.createdAt }!!.createdAt
        AudioUpdateManager.setLastUpdate(lastUpdate)
      }
    }

  }

  fun downloadAudio(audio: Audio): Observable<Int> {
    var received = 0L
    var fileSize = audio.fileSize
    return Observable.create { s ->
      libraryClient.downloadAudio(audio.fileUrl).enqueue(object : Callback<ResponseBody> {
        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
          t.printStackTrace()
        }

        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
          CoroutineScope(Dispatchers.IO).launch {
            val apkFile = audio.getFile(context)
            try {
              var outputStream: OutputStream? = null
              var inputStream = response.body()!!.byteStream()
              try {
                val fileReader = ByteArray(1048576)
                outputStream = FileOutputStream(apkFile)
                var lastProgress = 0
                while (true) {
                  val read = inputStream.read(fileReader)
                  if (read == -1) {
                    break
                  }
                  outputStream.write(fileReader, 0, read)
                  received += read.toLong()
                  val progress = ((received.toDouble() / fileSize.toDouble()) * 100).toInt()
                  if (progress / 2 != lastProgress / 2) s.onNext(progress)
                  lastProgress = progress
                }
                outputStream.flush()
                inputStream.close()
                outputStream.close()
                s.onComplete()
                audio.isDownloaded = true
                libraryDao.insertAudio(audio)
              } catch (e: Exception) {
                e.printStackTrace()
                inputStream.close()
                outputStream?.close()
                s.onError(RuntimeException("Download failed"))
              }
            } catch (e: Exception) {
              e.printStackTrace()
              s.onError(RuntimeException("Download failed"))
            }

          }
        }

      })
    }

  }

}

