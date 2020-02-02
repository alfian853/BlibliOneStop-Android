package com.gdn.android.onestop.chat.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        GroupChat::class,
        Group::class,
        GroupInfo::class,
        GroupMeeting::class,
        MeetingNote::class,
        PersonalChat::class,
        PersonalInfo::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ChatDatabase : RoomDatabase(){

    abstract fun chatDao() : ChatDao
    abstract fun groupDao() : GroupDao
}