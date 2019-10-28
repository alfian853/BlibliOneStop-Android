package com.gdn.android.onestop.group.data

import androidx.room.PrimaryKey

class GroupChatResponse {
    lateinit var id: String
    lateinit var groupId : String
    lateinit var username: String
    lateinit var text: String
    var createdAt: Long = 0

    var isReply : Boolean = false
    var repliedId: String? = null
    var repliedText: String? = null
    var isMeeting : Boolean = false
    var meetingDate: Long? = null
}