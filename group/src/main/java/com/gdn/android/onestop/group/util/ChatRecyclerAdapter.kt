package com.gdn.android.onestop.group.util

import android.animation.ValueAnimator
import android.text.format.DateUtils
import android.util.Log
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.gdn.android.onestop.base.util.*
import com.gdn.android.onestop.group.R
import com.gdn.android.onestop.group.data.Group
import com.gdn.android.onestop.group.data.GroupChat
import com.gdn.android.onestop.group.databinding.*
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.layout_date_separator.view.*
import java.util.*


class ChatRecyclerAdapter(
  val profileClickCallback: ItemClickCallback<String>
) : RecyclerView.Adapter<ChatRecyclerAdapter.BaseChatViewHolder>(){

  lateinit var lastBindItem: GroupChat

  var chatList : List<GroupChat> = LinkedList()
    set(value){
      field = value
    }

  var itemViewArray : SparseArray<View> = SparseArray()

  lateinit var repliedClickCallback: ItemClickCallback<GroupChat>
  lateinit var meetingNoteClickCallback: ItemClickCallback<GroupChat>

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
          ItemChatBinding.inflate(layoutInflater, parent, false)
      )
      MY_MESSAGE_TYPE -> return MyChatViewHolder(
          ItemChatUserBinding.inflate(layoutInflater, parent, false)
      )
      REPLY_TYPE -> return ChatReplyViewHolder(
          ItemChatReplyBinding.inflate(layoutInflater, parent, false)
      )
      MY_REPLY_TYPE -> return MyChatReplyViewHolder(
          ItemChatReplyUserBinding.inflate(layoutInflater, parent, false)
      )
      MEETING_TYPE -> return MeetingViewHolder(
          ItemChatMeetingBinding.inflate(layoutInflater, parent, false)
      )
      MY_MEETING_TYPE -> return MyMeetingViewHolder(
          ItemChatMeetingUserBinding.inflate(layoutInflater, parent, false)
      )

    }
    return ChatViewHolder(
        ItemChatBinding.inflate(layoutInflater, parent, false)
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
    holder.onBindViewHolder(chat, position)
  }

  abstract inner class BaseChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    abstract fun onBindViewHolder(chat: GroupChat, position: Int)

    fun setDateSeparator(dateContainer: CardView, dateText: TextView, chat: GroupChat, position: Int){
      val isDiffDay = if(position == 0){true}
      else chat.dayOfYear != chatList[position-1].dayOfYear

      if(isDiffDay){
        dateContainer.visibility = View.VISIBLE
          dateText.text = chat.dayOfYear
      }
      else{
        dateContainer.visibility = View.GONE
      }

    }
  }

  inner class ChatViewHolder(binding: ItemChatBinding) : BaseChatViewHolder(binding.root){
    private val tvName: TextView = binding.tvUsername
    private val tvMessage: TextView = binding.tvMessage
    private val tvDate: TextView = binding.tvDate
    private val tvNamePict: TextView = binding.ivUser
    private val cvBatchDate: CardView = binding.batchDate.cvBatchDate
    private val tvBatchDate: TextView = binding.batchDate.tvBatchDate

    override fun onBindViewHolder(chat: GroupChat, position: Int){
      tvName.text = chat.username
      tvName.setTextColor(chat.nameColor)
      tvDate.text = chat.createdAt.toDateString()
      tvMessage.text = chat.text
      val nameAlias = chat.username.toAliasName()
      tvNamePict.text = nameAlias
      tvNamePict.setBackgroundColor(chat.nameColor)

      tvMessage.maxWidth = chatMaxWidth

      setDateSeparator(cvBatchDate, tvBatchDate, chat, position)

      tvName.setOnClickListener { profileClickCallback.onItemClick(chat.username, position) }
      tvNamePict.setOnClickListener { profileClickCallback.onItemClick(chat.username, position) }
    }
  }

  inner class MyChatViewHolder(binding: ItemChatUserBinding) : BaseChatViewHolder(binding.root){
    private val tvMessage: TextView = binding.tvMessage
    private val tvDate: TextView = binding.tvDate
    private val pbSending: ProgressBar = binding.pbSending
    private val cvBatchDate: CardView = binding.batchDate.cvBatchDate
    private val tvBatchDate: TextView = binding.batchDate.tvBatchDate

    override fun onBindViewHolder(chat: GroupChat, position: Int) {
      tvMessage.text = chat.text
      tvDate.text = chat.createdAt.toDateString()

      if(chat.isSending){
        tvDate.visibility = View.GONE
        pbSending.visibility = View.VISIBLE
      }
      else{
        tvDate.visibility = View.VISIBLE
        pbSending.visibility = View.GONE
      }

      setDateSeparator(cvBatchDate, tvBatchDate, chat, position)

      tvMessage.maxWidth = myChatMaxWidth
    }
  }

  inner class ChatReplyViewHolder(binding: ItemChatReplyBinding) : BaseChatViewHolder(binding.root){
    private val tvName: TextView = binding.tvUsername
    private val tvMessage: TextView = binding.tvMessage
    private val tvDate: TextView = binding.tvDate
    private val tvNamePict: TextView = binding.ivUser
    private val tvReplyName: TextView = binding.tvReplyUsername
    private val tvReplyText: TextView = binding.tvReplyMessage
    private val llReplyContainer: LinearLayout = binding.llReplyContainer
    private val cvBatchDate: CardView = binding.batchDate.cvBatchDate
    private val tvBatchDate: TextView = binding.batchDate.tvBatchDate

    override fun onBindViewHolder(chat: GroupChat, position: Int){
      tvName.text = chat.username
      tvName.setTextColor(chat.nameColor)
      tvDate.text = chat.createdAt.toDateString()
      tvMessage.text = chat.text
      val nameAlias = chat.username.toAliasName()
      tvNamePict.text = nameAlias
      tvNamePict.setBackgroundColor(chat.nameColor)

      tvReplyName.text = chat.repliedUsername
      tvReplyText.text = chat.repliedText
      tvReplyName.setTextColor(chat.repliedNameColor)
      llReplyContainer.setOnClickListener{
        repliedClickCallback.onItemClick(chat, position)
      }

      tvMessage.maxWidth = chatMaxWidth

      setDateSeparator(cvBatchDate, tvBatchDate, chat, position)

      tvName.setOnClickListener { profileClickCallback.onItemClick(chat.username, position) }
      tvNamePict.setOnClickListener { profileClickCallback.onItemClick(chat.username, position) }
    }

  }

  inner class MyChatReplyViewHolder(binding: ItemChatReplyUserBinding) : BaseChatViewHolder(binding.root){
    private val tvMessage: TextView = binding.tvMessage
    private val tvDate: TextView = binding.tvDate
    private val pbSending: ProgressBar = itemView.findViewById(R.id.pb_sending)
    private val tvReplyName: TextView = binding.tvReplyUsername
    private val tvReplyText: TextView = binding.tvReplyMessage
    private val llReplyContainer: LinearLayout = binding.llReplyContainer
    private val cvBatchDate: CardView = binding.batchDate.cvBatchDate
    private val tvBatchDate: TextView = binding.batchDate.tvBatchDate

    override fun onBindViewHolder(chat: GroupChat, position: Int) {
      tvMessage.text = chat.text
      tvDate.text = chat.createdAt.toDateString()

      if(chat.isSending){
        tvDate.visibility = View.GONE
        pbSending.visibility = View.VISIBLE
      }
      else{
        tvDate.visibility = View.VISIBLE
        pbSending.visibility = View.GONE
      }

      setDateSeparator(cvBatchDate, tvBatchDate, chat, position)

      tvReplyName.text = chat.repliedUsername
      tvReplyText.text = chat.repliedText
      tvReplyName.setTextColor(chat.repliedNameColor)
      llReplyContainer.setOnClickListener{
        repliedClickCallback.onItemClick(chat, position)
      }
      tvMessage.maxWidth = myChatMaxWidth
    }
  }

  inner class MeetingViewHolder(binding: ItemChatMeetingBinding) : BaseChatViewHolder(binding.root){
    private val tvName: TextView = binding.tvUsername
    private val tvMessage: TextView = binding.tvMessage
    private val tvTitle: TextView = binding.tvMeetingTitle
    private val tvDate: TextView = binding.tvDate
    private val tvNamePict: TextView = binding.ivUser
    private val tvMeetingDate: TextView = binding.tvMeetingDate
    private val btnSeeNote: MaterialButton = binding.btnSeeNote
    private val cvBatchDate: CardView = binding.batchDate.cvBatchDate
    private val tvBatchDate: TextView = binding.batchDate.tvBatchDate

    override fun onBindViewHolder(chat: GroupChat, position: Int) {
      tvTitle.text = "Meeting #${chat.meetingNo}"
      tvName.text = chat.username
      tvName.setTextColor(chat.nameColor)
      tvDate.text = chat.createdAt.toDateString()
      tvMessage.text = chat.text
      val nameAlias = chat.username.toAliasName()
      tvNamePict.text = nameAlias
      tvNamePict.setBackgroundColor(chat.nameColor)

      tvMeetingDate.text = "Date: "+chat.meetingDate?.toDateTime24String()
      btnSeeNote.setOnClickListener {
        meetingNoteClickCallback.onItemClick(chat, position)
      }

      setDateSeparator(cvBatchDate, tvBatchDate, chat, position)

      tvMessage.maxWidth = chatMaxWidth

      tvName.setOnClickListener { profileClickCallback.onItemClick(chat.username, position) }
      tvNamePict.setOnClickListener { profileClickCallback.onItemClick(chat.username, position) }
    }

  }

  inner class MyMeetingViewHolder(binding: ItemChatMeetingUserBinding) : BaseChatViewHolder(binding.root){
    private val tvMessage: TextView = binding.tvMessage
    private val tvTitle: TextView = binding.tvMeetingTitle
    private val tvDate: TextView = binding.tvDate
    private val pbSending: ProgressBar = binding.pbSending
    private val tvMeetingDate: TextView = binding.tvMeetingDate
    private val btnSeeNote: MaterialButton = binding.btnSeeNote
    private val cvBatchDate: CardView = binding.batchDate.cvBatchDate
    private val tvBatchDate: TextView = binding.batchDate.tvBatchDate

    override fun onBindViewHolder(chat: GroupChat, position: Int) {
      tvTitle.text = "Meeting #${chat.meetingNo}"
      tvMessage.text = chat.text
      tvDate.text = chat.createdAt.toDateString()

      if(chat.isSending){
        tvDate.visibility = View.GONE
        pbSending.visibility = View.VISIBLE
      }
      else{
        tvDate.visibility = View.VISIBLE
        pbSending.visibility = View.GONE
      }

      setDateSeparator(cvBatchDate, tvBatchDate, chat, position)

      tvMeetingDate.text = "Date: "+chat.meetingDate?.toDateTime24String()
      btnSeeNote.setOnClickListener {
        meetingNoteClickCallback.onItemClick(chat, position)
      }

      tvMessage.maxWidth = myChatMaxWidth
    }
  }


}