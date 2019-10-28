package com.gdn.android.onestop.group.data

import java.io.Serializable

class ChatSendRequest : Serializable{

    lateinit var groupId: String

    lateinit var text: String

    var isReply = false
    var repliedText: String? = null

    var repliedId: String? = null

    var isMeeting = false
    var meetingDate: Long? = null
}