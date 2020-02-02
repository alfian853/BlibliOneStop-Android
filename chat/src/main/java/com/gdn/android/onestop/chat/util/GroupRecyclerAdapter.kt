package com.gdn.android.onestop.chat.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.gdn.android.onestop.base.util.ItemClickCallback
import com.gdn.android.onestop.chat.data.Group
import com.gdn.android.onestop.chat.data.GroupDao
import com.gdn.android.onestop.chat.data.GroupInfo
import com.gdn.android.onestop.chat.databinding.ItemGroupBinding

class GroupRecyclerAdapter(
    private val optionClickCallback: ItemClickCallback<Group>,
    private val nameClickCallback: ItemClickCallback<Group>
    ) : RecyclerView.Adapter<GroupRecyclerAdapter.GroupViewHolder>() {

    private var groupList: List<Group> = ArrayList()

    fun updateList(groupList: List<Group>){
        this.groupList = groupList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        return GroupViewHolder(ItemGroupBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ))
    }

    override fun getItemCount(): Int {
        return groupList.size
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
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


    inner class GroupViewHolder(databinding: ItemGroupBinding) : RecyclerView.ViewHolder(databinding.root) {

        val tvName: TextView = databinding.tvGroupName
        val ivOption: ImageView = databinding.ivOption
        val ivMute: ImageView = databinding.ivMute
        val cvUnread: CardView = databinding.cvUnread
        val tvUnread: TextView = databinding.tvUnread
    }

}