package com.gdn.android.onestop.group.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
class GroupChat {
    @PrimaryKey
    lateinit var id: String
    lateinit var username: String
    lateinit var text: String
    var createdAt: Long = 0

    var isReply : Boolean = false
    lateinit var repliedId: String
    lateinit var repliedText: String
    var isMeeting : Boolean = false
    var meetingDate: Long = 0
}