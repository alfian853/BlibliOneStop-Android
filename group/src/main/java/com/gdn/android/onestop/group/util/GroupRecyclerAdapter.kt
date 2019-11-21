package com.gdn.android.onestop.group.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gdn.android.onestop.group.R
import com.gdn.android.onestop.group.data.Group
import com.gdn.android.onestop.base.util.ItemClickCallback
import com.gdn.android.onestop.group.databinding.FragmentGroupBinding
import com.gdn.android.onestop.group.databinding.ItemGroupBinding
import kotlin.collections.ArrayList

class GroupRecyclerAdapter : RecyclerView.Adapter<GroupRecyclerAdapter.GroupViewHolder>() {

    var groupList : List<Group> = ArrayList()
    lateinit var nameClickCallback: ItemClickCallback<Group>
    lateinit var optionClickCallback: ItemClickCallback<Group>

    fun updateList(groupList : List<Group>){
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
    }


    inner class GroupViewHolder(databinding: ItemGroupBinding) : RecyclerView.ViewHolder(databinding.root) {

        val tvName: TextView = databinding.tvGroupName
        val ivOption: ImageView = databinding.ivOption
        val ivMute: ImageView = databinding.ivMute
    }

}