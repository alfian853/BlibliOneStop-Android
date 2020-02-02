package com.gdn.android.onestop.chat.data

import com.google.gson.annotations.SerializedName

class GroupChatResponse {
    lateinit var id: String
    lateinit var groupId : String
    lateinit var username: String
    lateinit var text: String
    var createdAt: Long = 0

    @SerializedName("isReply")
    var isReply : Boolean = false
    var repliedId: String? = null
    var repliedText: String? = null
    var repliedUsername: String? = null

    @SerializedName("isMeeting")
    var isMeeting : Boolean = false
    var meetingDate: Long? = null
    var meetingNo: Int? = null

}