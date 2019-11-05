package com.gdn.android.onestop.group.util

import android.animation.ValueAnimator
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gdn.android.onestop.base.util.ItemClickCallback
import com.gdn.android.onestop.base.util.toAliasName
import com.gdn.android.onestop.group.R
import com.gdn.android.onestop.group.data.GroupChat
import com.gdn.android.onestop.base.util.toTimeString
import java.util.*


class ChatRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    var chatList : List<GroupChat> = LinkedList()
        set(value){
            field = value
        }

    var itemViewArray : SparseArray<View> = SparseArray()

    lateinit var repliedClickCallback : ItemClickCallback<GroupChat>

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
        val MSG_MAXW_RATIO = 0.65
        val MY_MSG_MAXW_RATIO = 0.75
    }

    fun updateChatList(chatList : List<GroupChat>){
        this.chatList = chatList
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        val chat = chatList[position]
        return if(chat.isMe){
            if(chat.isReply) MY_REPLY_TYPE
            else MY_MESSAGE_TYPE
        }
        else {
            if(chat.isReply) REPLY_TYPE
            else MESSAGE_TYPE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        when(viewType){
            MESSAGE_TYPE -> return ChatViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
            )
            MY_MESSAGE_TYPE -> return MyChatViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_chat_user, parent, false)
            )
            REPLY_TYPE -> return ChatReplyViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_chat_reply, parent, false)
            )
            MY_REPLY_TYPE -> return MyChatReplyViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_chat_reply_user, parent, false)
            )
        }
        return ChatViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val chat = chatList[position]
        itemViewArray.put(position, holder.itemView)
        pendingReplyShowAnimation?.let {
            if(it.position == position){
                pendingReplyShowAnimation?.animate(holder.itemView)
            }
            pendingReplyShowAnimation = null
        }

        if(holder is ChatViewHolder){
            holder.tvName.text = chat.username
            holder.tvName.setTextColor(chat.nameColor)
            holder.tvDate.text = chat.createdAt.toTimeString()
            holder.tvMessage.text = chat.text
            val nameAlias = chat.username.toAliasName()
            holder.tvNamePict.text = nameAlias
            holder.tvNamePict.setBackgroundColor(chat.nameColor)

            if(chat.isReply){
                holder as ChatReplyViewHolder
                holder.tvReplyName.text = chat.repliedUsername
                holder.tvReplyText.text = chat.repliedText
                holder.tvReplyName.setTextColor(chat.repliedNameColor)
                holder.clReplyContainer.setOnClickListener{
                    repliedClickCallback.onItemClick(chat, position)
                }
            }
            holder.tvMessage.maxWidth = chatMaxWidth
        }
        else if(holder is MyChatViewHolder){
            holder.tvMessage.text = chat.text
            holder.tvDate.text = chat.createdAt.toTimeString()

            if(chat.isSending){
                holder.tvDate.visibility = View.GONE
                holder.pbSending.visibility = View.VISIBLE
            }
            else{
                holder.tvDate.visibility = View.VISIBLE
                holder.pbSending.visibility = View.GONE
            }

            if(chat.isReply){
                holder as MyChatReplyViewHolder
                holder.tvReplyName.text = chat.repliedUsername
                holder.tvReplyText.text = chat.repliedText
                holder.tvReplyName.setTextColor(chat.repliedNameColor)
                holder.clReplyContainer.setOnClickListener{
                    repliedClickCallback.onItemClick(chat, position)
                }
            }
            holder.tvMessage.maxWidth = myChatMaxWidth
        }

    }


    open inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val tvName : TextView = itemView.findViewById(R.id.tv_username)
        val tvMessage : TextView =itemView.findViewById(R.id.tv_message)
        val tvDate : TextView = itemView.findViewById(R.id.tv_date)
        val tvNamePict : TextView = itemView.findViewById(R.id.iv_user)
    }

    open inner class MyChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val tvMessage : TextView =itemView.findViewById(R.id.tv_message)
        val tvDate : TextView = itemView.findViewById(R.id.tv_date)
        val pbSending : ProgressBar = itemView.findViewById(R.id.pb_sending)
    }

    inner class ChatReplyViewHolder(itemView: View) : ChatViewHolder(itemView){
        val tvReplyName : TextView = itemView.findViewById(R.id.tv_reply_username)
        val tvReplyText : TextView = itemView.findViewById(R.id.tv_reply_message)
        val clReplyContainer : LinearLayout = itemView.findViewById(R.id.ll_reply_container)
    }

    inner class MyChatReplyViewHolder(itemView: View) : MyChatViewHolder(itemView){
        val tvReplyName : TextView = itemView.findViewById(R.id.tv_reply_username)
        val tvReplyText : TextView = itemView.findViewById(R.id.tv_reply_message)
        val clReplyContainer : LinearLayout = itemView.findViewById(R.id.ll_reply_container)
    }

}