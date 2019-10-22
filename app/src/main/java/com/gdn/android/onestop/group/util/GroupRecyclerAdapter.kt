package com.gdn.android.onestop.group.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gdn.android.onestop.R
import com.gdn.android.onestop.group.data.Group
import kotlin.collections.ArrayList

class GroupRecyclerAdapter : RecyclerView.Adapter<GroupRecyclerAdapter.GroupViewHolder>() {

    var groupList : List<Group> = ArrayList()

    fun updateList(groupList : List<Group>){
        this.groupList = groupList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_group, parent, false)

        return GroupViewHolder(view)
    }

    override fun getItemCount(): Int {
        return groupList.size
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val group = groupList[position]

        holder.tvName.text = group.name
    }


    inner class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvName: TextView = itemView.findViewById(R.id.tv_group_name)
        val tvOption: TextView = itemView.findViewById(R.id.tv_option)
    }

}