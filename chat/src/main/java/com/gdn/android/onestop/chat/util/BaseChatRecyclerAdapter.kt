package com.gdn.android.onestop.chat.util

import android.animation.ValueAnimator
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gdn.android.onestop.base.util.ItemClickCallback
import com.gdn.android.onestop.chat.data.PersonalChat
import com.gdn.android.onestop.chat.databinding.ItemChatBinding
import com.gdn.android.onestop.chat.databinding.ItemChatReplyBinding
import com.gdn.android.onestop.chat.databinding.ItemChatReplyUserBinding
import com.gdn.android.onestop.chat.databinding.ItemChatUserBinding
import com.gdn.android.onestop.chat.util.Const.MESSAGE_TYPE
import com.gdn.android.onestop.chat.util.Const.MY_MESSAGE_TYPE
import com.gdn.android.onestop.chat.util.Const.MY_REPLY_TYPE
import com.gdn.android.onestop.chat.util.Const.REPLY_TYPE
import java.util.*


open class BaseChatRecyclerAdapter<T : PersonalChat>(
  private val onProfileClick: ItemClickCallback<String>,
  private val onMessageLongClick: ItemClickCallback<String>,
  private val repliedClickCallback: ItemClickCallback<T>
) : RecyclerView.Adapter<BaseChatViewHolder<T>>(){

  lateinit var lastBindItem: T

  var chatList : List<T> = LinkedList()
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
    private const val MSG_MAXW_RATIO = 0.65
    private const val MY_MSG_MAXW_RATIO = 0.75
  }

  fun updateChatList(chatList : List<T>){
    this.chatList = chatList
    notifyDataSetChanged()
  }

  override fun getItemViewType(position: Int): Int {
    val chat = chatList[position]
    return if(chat.isMe){
      when {
        chat.isReply -> MY_REPLY_TYPE
        else -> MY_MESSAGE_TYPE
      }
    }
    else {
      when {
        chat.isReply -> REPLY_TYPE
        else -> MESSAGE_TYPE
      }
    }
  }

  protected fun createViewHolderByType(parent: ViewGroup, viewType: Int): BaseChatViewHolder<T>? {
    val layoutInflater = LayoutInflater.from(parent.context)

    return when(viewType){
      MESSAGE_TYPE -> ChatViewHolder(
        ItemChatBinding.inflate(layoutInflater, parent, false),
        onProfileClick, onMessageLongClick
      )
      MY_MESSAGE_TYPE -> MyChatViewHolder(
        ItemChatUserBinding.inflate(layoutInflater, parent, false),
        onMessageLongClick
      )
      REPLY_TYPE -> ChatReplyViewHolder(
        ItemChatReplyBinding.inflate(layoutInflater, parent, false),
        onProfileClick, onMessageLongClick, repliedClickCallback
      )
      MY_REPLY_TYPE -> MyChatReplyViewHolder(
        ItemChatReplyUserBinding.inflate(layoutInflater, parent, false),
        onMessageLongClick, repliedClickCallback
      )
      else -> null
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseChatViewHolder<T> {
    val layoutInflater = LayoutInflater.from(parent.context)

    return createViewHolderByType(parent, viewType)?:ChatViewHolder(
      ItemChatBinding.inflate(layoutInflater, parent, false),
      onProfileClick, onMessageLongClick
    )

  }

  override fun getItemCount(): Int {
    return chatList.size
  }

  override fun onBindViewHolder(holder: BaseChatViewHolder<T>, position: Int) {

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