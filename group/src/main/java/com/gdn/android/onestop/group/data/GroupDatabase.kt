package com.gdn.android.onestop.group.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [PersonalChat::class, GroupChat::class, Group::class, GroupInfo::class, GroupMeeting::class, MeetingNote::class],
    version = 1,
    exportSchema = false
)
abstract class GroupDatabase : RoomDatabase(){

    abstract fun chatDao() : ChatDao
    abstract fun groupDao() : GroupDao
}