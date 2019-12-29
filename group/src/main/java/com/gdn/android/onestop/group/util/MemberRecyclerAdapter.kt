package com.gdn.android.onestop.group.util

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gdn.android.onestop.base.util.ItemClickCallback
import com.gdn.android.onestop.base.util.Util
import com.gdn.android.onestop.base.util.toAliasName
import com.gdn.android.onestop.group.databinding.ItemMemberBinding
import java.util.*

class MemberRecyclerAdapter(
  private val itemClickCallback: ItemClickCallback<String>
) : RecyclerView.Adapter<MemberRecyclerAdapter.MemberViewHolder>(){

  var members: List<String> = ArrayList()

  fun updateData(members: List<String>){
    this.members = members
    notifyDataSetChanged()
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
    return MemberViewHolder(
      ItemMemberBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )
  }

  override fun getItemCount(): Int {
    return members.size
  }

  override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
    val username = members[position]
    val alias = username.toAliasName()
    holder.ivUsername.text = alias
    holder.ivUsername.setBackgroundColor(Util.getColorFromString(username))
    holder.tvUsername.text = username

    holder.itemView.setOnClickListener {
      itemClickCallback.onItemClick(username, position)
    }
  }

  inner class MemberViewHolder(binding: ItemMemberBinding) : RecyclerView.ViewHolder(binding.root){
    val ivUsername: TextView = binding.ivUser
    val tvUsername: TextView = binding.tvUsername
  }

}