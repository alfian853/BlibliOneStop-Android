package com.gdn.android.onestop.library.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LibraryDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertBook(vararg book: Book)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertBook(bookList: List<Book>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAudio(vararg book: Audio)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAudio(bookList: List<Audio>)

  @Query("select * from Book order by isBookmarked DESC,title ASC")
  fun getBooksLiveData(): LiveData<List<Book>>

  @Query("select * from Audio order by isBookmarked DESC,title ASC")
  fun getAudiosLiveData(): LiveData<List<Audio>>

}