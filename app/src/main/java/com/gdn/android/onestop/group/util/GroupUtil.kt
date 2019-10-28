package com.gdn.android.onestop.group.util

import com.gdn.android.onestop.group.data.GroupChat
import com.gdn.android.onestop.group.data.GroupChatResponse
import com.gdn.android.onestop.util.toDateString


class GroupUtil {

    companion object {
        fun mapChatResponse(chatResponse: GroupChatResponse) : GroupChat {
            val groupChat = GroupChat()
            groupChat.id = chatResponse.id
            groupChat.username = chatResponse.username
            groupChat.text = chatResponse.text

            groupChat.createdAt = chatResponse.createdAt.toDateString()
            chatResponse.meetingDate?.let {
                groupChat.meetingDate = it.toDateString()
            }
            groupChat.isMeeting = chatResponse.isMeeting

            groupChat.isReply = chatResponse.isReply
            groupChat.repliedId = chatResponse.repliedId
            groupChat.repliedText = chatResponse.repliedText

            return groupChat
        }
    }
}

