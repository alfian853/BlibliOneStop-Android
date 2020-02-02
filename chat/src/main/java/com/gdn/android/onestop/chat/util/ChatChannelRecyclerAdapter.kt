package com.gdn.android.onestop.chat.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.gdn.android.onestop.base.util.ItemClickCallback
import com.gdn.android.onestop.chat.data.ChatChannel
import com.gdn.android.onestop.chat.databinding.ItemGroupBinding

class ChatChannelRecyclerAdapter(
    private val optionClickCallback: ItemClickCallback<ChatChannel>,
    private val nameClickCallback: ItemClickCallback<ChatChannel>
    ) : RecyclerView.Adapter<ChatChannelRecyclerAdapter.ChatChannelViewHolder>() {

    private var groupList: List<ChatChannel> = ArrayList()

    fun updateList(groupList: List<ChatChannel>){
        this.groupList = groupList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatChannelViewHolder {
        return ChatChannelViewHolder(ItemGroupBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ))
    }

    override fun getItemCount(): Int {
        return groupList.size
    }

    override fun onBindViewHolder(holder: ChatChannelViewHolder, position: Int) {
        val group = groupList[position]

        holder.tvName.text = group.name

        holder.tvName.setOnClickListener{
            nameClickCallback.onItemClick(group, position)
        }
        holder.ivOption.setOnClickListener {
            optionClickCallback.onItemClick(group, position)
        }

        holder.ivMute.visibility = if(group.isMute)View.VISIBLE
                                   else View.GONE


        if(group.unreadChat > 0){
            holder.cvUnread.visibility = View.VISIBLE
            holder.tvUnread.text = group.unreadChat.toString()
        }
        else{
            holder.cvUnread.visibility = View.GONE
        }

    }


    inner class ChatChannelViewHolder(databinding: ItemGroupBinding) : RecyclerView.ViewHolder(databinding.root) {

        val tvName: TextView = databinding.tvGroupName
        val ivOption: ImageView = databinding.ivOption
        val ivMute: ImageView = databinding.ivMute
        val cvUnread: CardView = databinding.cvUnread
        val tvUnread: TextView = databinding.tvUnread
    }

}