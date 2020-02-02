package com.gdn.android.onestop.chat.util

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gdn.android.onestop.base.util.ItemClickCallback
import com.gdn.android.onestop.base.util.toDateTime24String
import com.gdn.android.onestop.chat.data.GroupMeeting
import com.gdn.android.onestop.chat.databinding.ItemMeetingBinding
import java.util.*

class MeetingListAdapter : RecyclerView.Adapter<MeetingListAdapter.MeetingViewHolder>(){

  private var meetingList: List<GroupMeeting> = LinkedList()
  var itemClickCallback: ItemClickCallback<GroupMeeting>? = null

  fun updateMeetingList(meetingList: List<GroupMeeting>){
    this.meetingList = meetingList
    notifyDataSetChanged()
  }

  override fun getItemCount(): Int {
    return meetingList.size
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeetingViewHolder {
    return MeetingViewHolder(
      ItemMeetingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )
  }

  override fun onBindViewHolder(holder: MeetingViewHolder, position: Int) {
    val meeting = meetingList[position]

    holder.meetingNo.text = "Meeting #${meeting.meetingNo}"
    holder.meetingDate.text = meeting.meetingDate.toDateTime24String()
    holder.tvGroup.text = meeting.groupName
    holder.ivEnter.setOnClickListener {
      itemClickCallback?.onItemClick(meeting, position)
    }
  }

  inner class MeetingViewHolder(binding: ItemMeetingBinding) : RecyclerView.ViewHolder(binding.root){
    val meetingNo: TextView = binding.tvMeetingNo
    val meetingDate: TextView = binding.tvMeetingDate
    val tvGroup: TextView = binding.tvGroup
    val ivEnter = binding.ivEnter
  }

}