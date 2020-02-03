package com.gdn.android.onestop.chat.data

import com.google.gson.annotations.SerializedName

class PersonalChatResponse {
    lateinit var id: String

    @SerializedName("_from")
    var from: String = ""

    @SerializedName("_to")
    var to: String = ""
    lateinit var text: String
    var createdAt: Long = 0

    @SerializedName("isReply")
    var isReply : Boolean = false
    var repliedId: String? = null
    var repliedText: String? = null
    var repliedUsername: String? = null

}