package com.gdn.android.onestop.chat.util

import android.animation.ValueAnimator
import android.opengl.Visibility
import android.util.Log
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gdn.android.onestop.base.util.ItemClickCallback
import com.gdn.android.onestop.base.util.toDateTime24String
import com.gdn.android.onestop.chat.data.MeetingNote
import com.gdn.android.onestop.chat.databinding.ItemMeetingNoteBinding
import java.util.*

class MeetingNoteRecyclerAdapter : RecyclerView.Adapter<MeetingNoteRecyclerAdapter.MeetingNoteViewHolder>() {

  var itemEditClickCallback: ItemClickCallback<MeetingNote>? = null

  private var meetingNoteList: List<MeetingNote> = LinkedList()

  var itemViewArray: SparseArray<View> = SparseArray()
  var animationTask: ItemAnimationTask? = null

  class ItemAnimationTask(
    var position : Int = 0,
    var animator : ValueAnimator
  ){
    fun animate(itemView : View){
      val backup = itemView.background
      animator.addUpdateListener {
        itemView.setBackgroundColor(it.animatedValue as Int)
        if(it.animatedValue as Int == 0){
          itemView.background = backup
        }
      }
      animator.start()
    }
  }


  fun updateData(meetingNoteList: List<MeetingNote>){
    this.meetingNoteList = meetingNoteList
    notifyDataSetChanged()
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeetingNoteViewHolder {
    return MeetingNoteViewHolder(
      ItemMeetingNoteBinding.inflate(
        LayoutInflater.from(parent.context), parent, false
      )
    )
  }

  override fun getItemCount(): Int {
    return meetingNoteList.size
  }

  override fun onBindViewHolder(holder: MeetingNoteViewHolder, position: Int) {
    val meetingNote = meetingNoteList[position]

    itemViewArray.put(position, holder.itemView)
    animationTask?.let {
      if(it.position == position){
        it.animate(holder.itemView)
        holder.tvNote.visibility = View.VISIBLE

      }
      animationTask = null
    }

    holder.tvDate.text = meetingNote.meetingDate.toDateTime24String()
    holder.tvMeetingNo.text = "Meeting #${meetingNote.meetingNumber}"

    holder.ivNoteEdit.setOnClickListener {
      itemEditClickCallback?.onItemClick(meetingNote, position)
    }
    holder.tvNote.text = meetingNote.note

    val onclick = View.OnClickListener {
      val tvNote = holder.tvNote
      tvNote.visibility = if(tvNote.visibility == View.GONE)View.VISIBLE
      else View.GONE
    }
    holder.tvMeetingNo.setOnClickListener(onclick)
    holder.tvDate.setOnClickListener(onclick)

  }

  inner class MeetingNoteViewHolder(binding: ItemMeetingNoteBinding): RecyclerView.ViewHolder(binding.root){
    val tvDate: TextView = binding.tvMeetingDate
    val tvMeetingNo: TextView = binding.tvMeetingNumber
    val ivNoteEdit: ImageView = binding.ivEditNote
    val tvNote: TextView = binding.tvMeetingNote
  }

}