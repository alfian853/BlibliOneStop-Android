package com.gdn.android.onestop.idea.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [IdeaPost::class, IdeaComment::class], version = 1, exportSchema = false)
abstract class IdeaDatabase : RoomDatabase(){

    abstract fun IdeaDao() : IdeaDao
}