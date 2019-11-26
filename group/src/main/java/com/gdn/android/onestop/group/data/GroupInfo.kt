package com.gdn.android.onestop.group.data

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
class GroupInfo {
    @PrimaryKey
    lateinit var id : String
    var lowerBoundTimeStamp : Long = Long.MAX_VALUE
    var hasFetchFirstChat = false
    var upperBoundTimeStamp : Long = Long.MIN_VALUE
    var isMute = false

    @Ignore
    fun isNeverFetched(): Boolean {
        return lowerBoundTimeStamp == Long.MAX_VALUE
    }
}