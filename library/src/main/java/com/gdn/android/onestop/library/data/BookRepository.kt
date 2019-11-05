package com.gdn.android.onestop.library.data

import android.content.Context
import android.os.Environment
import android.util.Log
import com.gdn.android.onestop.base.UrlConstant
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableEmitter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

class BookRepository(
    private val context : Context,
    private val libraryDao: LibraryDao,
    private val libraryClient: LibraryClient,
    private val bookUpdateManager: BookUpdateManager
){

    companion object {
        private const val BOOK_RES_PREFIX = "/books/"
    }

    fun getLibraryLiveData() = libraryDao.getAllBook()

    suspend fun doFetchLatestData(){

        val response = libraryClient.getBooks(bookUpdateManager.getLastUpdate())

        if(response.isSuccessful){
            val books = response.body()!!.data!!
            if(books.isNotEmpty()){
                libraryDao.insertBook(books)
                val lastUpdate = books.maxBy { it.createdAt }!!.createdAt
                bookUpdateManager.setLastUpdate(lastUpdate)
            }
        }

    }

    fun downloadBook(book : Book) : Observable<Int> {
        var filename = book.path
        var received = 0L
        var filesize = book.fileSize
        filename = filename.replace(BOOK_RES_PREFIX,"").replace(" ","")
        return Observable.create { s ->
            libraryClient.downloadBook(book.fileUrl)
                .enqueue(object : Callback<ResponseBody> {
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        t.printStackTrace()
                    }

                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        Log.d("book","enter this")
                        CoroutineScope(Dispatchers.IO).launch {
                            val apkFile = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)!!, filename)
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
                                        val progress = ((received.toDouble() / filesize.toDouble())*100).toInt()
                                        if(progress != lastProgress)s.onNext(progress)
                                        lastProgress = progress
                                    }
                                    outputStream.flush()
                                    inputStream.close()
                                    outputStream.close()
                                    s.onComplete()
                                    book.isDownloaded = true
                                    libraryDao.insertBook(book)
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

//                            val successWrite = writeResponseBodyToDisk(
//                                filename,
//                                response.body()!!.byteStream()
//                            )
//
//                            if(successWrite){
//                                received += response.body()!!.contentLength()
//                                val progress = ((received.toDouble() / filesize.toDouble())*100).toInt()
//                                Log.d("book","progress : $progress")
//                                if(progress == 100){
//                                    s.onComplete()
//                                }
//                                else{
//                                    s.onNext(progress)
//                                }
//                            }
//                            else{
//                            }
                        }
                    }

                })
        }

    }

    fun writeResponseBodyToDisk(filename : String, inputStream: InputStream): Boolean {
        val apkFile = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)!!, filename)
        try {
            var outputStream: OutputStream? = null
            try {
                val fileReader = ByteArray(1048576)
                var fileSizeDownloaded: Long = 0
                outputStream = FileOutputStream(apkFile)

                while (true) {
                    val read = inputStream.read(fileReader)
                    if (read == -1) {
                        break
                    }
                    outputStream.write(fileReader, 0, read)
                    fileSizeDownloaded += read.toLong()
                }
                outputStream.flush()
                inputStream.close()
                outputStream.close()
                return true
            } catch (e: Exception) {
                e.printStackTrace()
                inputStream.close()
                outputStream?.close()
                return false
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }


}

