package com.gdn.android.onestop.group.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Group::class, GroupInfo::class, GroupChat::class, MeetingNote::class], version = 1, exportSchema = false)
abstract class GroupDatabase : RoomDatabase(){

    abstract fun GroupDao() : GroupDao
}