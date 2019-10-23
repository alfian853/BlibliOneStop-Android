package com.gdn.android.onestop.group.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class GroupInfo {
    @PrimaryKey
    lateinit var id : String
    var lastChatUpdate : Long = 0
}