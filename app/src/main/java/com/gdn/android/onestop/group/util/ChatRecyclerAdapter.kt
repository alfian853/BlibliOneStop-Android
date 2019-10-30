package com.gdn.android.onestop.group.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gdn.android.onestop.R
import com.gdn.android.onestop.group.data.GroupChat
import com.gdn.android.onestop.util.toDateString
import java.util.*


class ChatRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    var chatList : List<GroupChat> = LinkedList()

    companion object {
        private const val MESSAGE_TYPE = 0
        private const val MY_MESSAGE_TYPE = 1
        private const val REPLY_TYPE = 2
        private const val MY_REPLY_TYPE = 3
        private const val MEETING_TYPE = 4
        private const val MY_MEETING_TYPE = 5
    }

    fun updateChatList(chatList : List<GroupChat>){
        this.chatList = chatList
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        val chat = chatList[position]
        return if(chat.isMe){
            if(chat.isReply) MY_REPLY_TYPE
            else if(chat.isMeeting) MY_MEETING_TYPE
            else MY_MESSAGE_TYPE
        }
        else {
            if(chat.isReply) REPLY_TYPE
            else if(chat.isMeeting) MEETING_TYPE
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
        }
        return ChatViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false))
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val chat = chatList[position]
        if(holder is ChatViewHolder){
            holder.tvName.text = chat.username
            holder.tvDate.text = chat.createdAt.toDateString()
            holder.tvMessage.text = chat.text
            Glide.with(holder.itemView)
                .load(R.drawable.ic_default_user)
                .into(holder.ivUser)
        }
        else if(holder is MyChatViewHolder){
            holder.tvMessage.text = chat.text
            holder.tvDate.text = chat.createdAt.toDateString()
            holder.pbSending.visibility = if(chat.isSending)View.VISIBLE
                                        else View.GONE
        }
    }


    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val tvName : TextView = itemView.findViewById(R.id.tv_username)
        val tvMessage : TextView =itemView.findViewById(R.id.tv_message)
        val tvDate : TextView = itemView.findViewById(R.id.tv_date)
        val ivUser : ImageView = itemView.findViewById(R.id.iv_user)
    }

    inner class MyChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val tvMessage : TextView =itemView.findViewById(R.id.tv_message)
        val tvDate : TextView = itemView.findViewById(R.id.tv_date)
        val pbSending : ProgressBar = itemView.findViewById(R.id.pb_sending)
    }

    //TODO add remain view holder


}