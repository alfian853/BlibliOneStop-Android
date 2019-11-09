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

  @Query("select * from Book order by isBookmarked DESC,title ASC")
  fun getAllBook(): LiveData<List<Book>>

}