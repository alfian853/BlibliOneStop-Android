package com.gdn.android.onestop.group.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
class Chat {
    @PrimaryKey
    lateinit var id: String
    lateinit var username: String
    lateinit var text: String
    lateinit var createdAt: Date

    var isReply : Boolean = false
    lateinit var repliedId: String
    lateinit var repliedText: String
    var isMeeting : Boolean = false
    lateinit var meetingDate: Date
}