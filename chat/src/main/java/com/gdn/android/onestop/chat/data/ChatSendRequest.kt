package com.gdn.android.onestop.chat.data

import java.io.Serializable

class ChatSendRequest : Serializable{

    var text: String = ""

    var isReply = false
    var repliedText: String? = null
    var repliedId: String? = null
    var repliedUsername: String? = null

    var isMeeting = false
    var meetingDate: Long? = null
}