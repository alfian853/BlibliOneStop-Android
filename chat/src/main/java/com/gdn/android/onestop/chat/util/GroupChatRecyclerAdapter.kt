package com.gdn.android.onestop.chat.util

import android.animation.ValueAnimator
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gdn.android.onestop.base.util.*
import com.gdn.android.onestop.chat.data.GroupChat
import com.gdn.android.onestop.chat.R
import com.gdn.android.onestop.chat.databinding.*
import com.google.android.material.button.MaterialButton
import java.util.*


class GroupChatRecyclerAdapter(
  private val onProfileClick: ItemClickCallback<String>,
  private val onMessageLongClick: ItemClickCallback<String>,
  private val repliedClickCallback: ItemClickCallback<GroupChat>,
  private val meetingNoteClickCallback: ItemClickCallback<GroupChat>
) : RecyclerView.Adapter<BaseChatViewHolder>(){

  lateinit var lastBindItem: GroupChat

  var chatList : List<GroupChat> = LinkedList()
    set(value){
      field = value
    }

  var itemViewArray : SparseArray<View> = SparseArray()

  var layoutWidth : Int = 0
    set(value) {
      field = value
      chatMaxWidth = (layoutWidth.toDouble() * MSG_MAXW_RATIO).toInt()
      myChatMaxWidth = (layoutWidth.toDouble() * MY_MSG_MAXW_RATIO).toInt()
    }

  var chatMaxWidth : Int = 0
  var myChatMaxWidth : Int = 0

  class ItemAnimationTask(
    var position : Int = 0,
    var animator : ValueAnimator
  ){
    fun animate(itemView : View){
      animator.addUpdateListener {
        itemView.setBackgroundColor(it.animatedValue as Int)
      }
      animator.start()
    }
  }

  var pendingReplyShowAnimation : ItemAnimationTask? = null

  companion object {
    private const val MESSAGE_TYPE = 0
    private const val MY_MESSAGE_TYPE = 1
    private const val REPLY_TYPE = 2
    private const val MY_REPLY_TYPE = 3
    private const val MEETING_TYPE = 4
    private const val MY_MEETING_TYPE = 5

    private const val MSG_MAXW_RATIO = 0.65
    private const val MY_MSG_MAXW_RATIO = 0.75
  }

  fun updateChatList(chatList : List<GroupChat>){
    this.chatList = chatList
    notifyDataSetChanged()
  }

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

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseChatViewHolder {
    val layoutInflater = LayoutInflater.from(parent.context)

    when(viewType){
      MESSAGE_TYPE -> return ChatViewHolder(
        ItemChatBinding.inflate(layoutInflater, parent, false),
        onProfileClick, onMessageLongClick, repliedClickCallback, meetingNoteClickCallback
      )
      MY_MESSAGE_TYPE -> return MyChatViewHolder(
        ItemChatUserBinding.inflate(layoutInflater, parent, false),
        onProfileClick, onMessageLongClick, repliedClickCallback, meetingNoteClickCallback
      )
      REPLY_TYPE -> return ChatReplyViewHolder(
        ItemChatReplyBinding.inflate(layoutInflater, parent, false),
        onProfileClick, onMessageLongClick, repliedClickCallback, meetingNoteClickCallback
      )
      MY_REPLY_TYPE -> return MyChatReplyViewHolder(
        ItemChatReplyUserBinding.inflate(layoutInflater, parent, false),
        onProfileClick, onMessageLongClick, repliedClickCallback, meetingNoteClickCallback
      )
      MEETING_TYPE -> return MeetingViewHolder(
        ItemChatMeetingBinding.inflate(layoutInflater, parent, false),
        onProfileClick, onMessageLongClick, repliedClickCallback, meetingNoteClickCallback
      )
      MY_MEETING_TYPE -> return MyMeetingViewHolder(
        ItemChatMeetingUserBinding.inflate(layoutInflater, parent, false),
        onProfileClick, onMessageLongClick, repliedClickCallback, meetingNoteClickCallback
      )

    }
    return ChatViewHolder(
      ItemChatBinding.inflate(layoutInflater, parent, false),
      onProfileClick, onMessageLongClick, repliedClickCallback, meetingNoteClickCallback
    )
  }

  override fun getItemCount(): Int {
    return chatList.size
  }

  override fun onBindViewHolder(holder: BaseChatViewHolder, position: Int) {

    val chat = chatList[position]
    itemViewArray.put(position, holder.itemView)
    pendingReplyShowAnimation?.let {
      if(it.position == position){
        pendingReplyShowAnimation?.animate(holder.itemView)
      }
      pendingReplyShowAnimation = null
    }
    lastBindItem = chat

    val isDiffDay = if(position == 0){true}
    else chat.dayOfYear != chatList[position-1].dayOfYear

    holder.onBindViewHolder(chat, position, isDiffDay, chatMaxWidth, myChatMaxWidth)
  }

}