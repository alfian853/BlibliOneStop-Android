package com.gdn.android.onestop.chat.util

import android.view.LayoutInflater
import android.view.ViewGroup
import com.gdn.android.onestop.base.util.ItemClickCallback
import com.gdn.android.onestop.chat.data.GroupChat
import com.gdn.android.onestop.chat.databinding.ItemChatBinding
import com.gdn.android.onestop.chat.databinding.ItemChatMeetingBinding
import com.gdn.android.onestop.chat.databinding.ItemChatMeetingUserBinding
import com.gdn.android.onestop.chat.util.Const.MEETING_TYPE
import com.gdn.android.onestop.chat.util.Const.MESSAGE_TYPE
import com.gdn.android.onestop.chat.util.Const.MY_MEETING_TYPE
import com.gdn.android.onestop.chat.util.Const.MY_MESSAGE_TYPE
import com.gdn.android.onestop.chat.util.Const.MY_REPLY_TYPE
import com.gdn.android.onestop.chat.util.Const.REPLY_TYPE


class GroupChatRecyclerAdapter(
  private val onProfileClick: ItemClickCallback<String>,
  private val onMessageLongClick: ItemClickCallback<String>,
  private val repliedClickCallback: ItemClickCallback<GroupChat>,
  private val meetingNoteClickCallback: ItemClickCallback<GroupChat>
) : BaseChatRecyclerAdapter<GroupChat>(
  onProfileClick, onMessageLongClick, repliedClickCallback
){

  override fun getItemViewType(position: Int): Int {
    val chat = chatList[position]
    return if(chat.isMe){
      when {
        chat.isReply -> MY_REPLY_TYPE
        chat.isMeeting -> MY_MEETING_TYPE
        else -> MY_MESSAGE_TYPE
      }
    }
    else {
      when {
        chat.isReply -> REPLY_TYPE
        chat.isMeeting -> MEETING_TYPE
        else -> MESSAGE_TYPE
      }
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseChatViewHolder<GroupChat> {
    val layoutInflater = LayoutInflater.from(parent.context)

    return createViewHolderByType(parent, viewType)?: when(viewType){
      MEETING_TYPE -> return MeetingViewHolder(
        ItemChatMeetingBinding.inflate(layoutInflater, parent, false),
        onProfileClick, onMessageLongClick, meetingNoteClickCallback
      )
      MY_MEETING_TYPE -> return MyMeetingViewHolder(
        ItemChatMeetingUserBinding.inflate(layoutInflater, parent, false),
         onMessageLongClick, meetingNoteClickCallback
      )
      else -> ChatViewHolder(
        ItemChatBinding.inflate(layoutInflater, parent, false),
        onProfileClick, onMessageLongClick
      )
    }

  }
}