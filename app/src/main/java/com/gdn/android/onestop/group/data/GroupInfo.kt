package com.gdn.android.onestop.group.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class GroupInfo {
    @PrimaryKey
    lateinit var id : String
    var lowerBoundTimeStamp : Long = 0
    var upperBoundTimeStamp : Long = 0


}