package com.gdn.android.onestop.group.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
class GroupChat {
    @PrimaryKey
    lateinit var id: String
    lateinit var groupId : String
    lateinit var username: String
    lateinit var text: String
    lateinit var createdAt: String

    var isMe : Boolean = false
    var isReply : Boolean = false
    var repliedId: String? = null
    var repliedText: String? = null
    var isMeeting : Boolean = false
    lateinit var meetingDate: String
}