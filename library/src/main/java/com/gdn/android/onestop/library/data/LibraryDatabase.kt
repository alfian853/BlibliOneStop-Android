package com.gdn.android.onestop.library.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Book::class], version = 1, exportSchema = false)
abstract class LibraryDatabase : RoomDatabase() {
  abstract fun libraryDao(): LibraryDao
}