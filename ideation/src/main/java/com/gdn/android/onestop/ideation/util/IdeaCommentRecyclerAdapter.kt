package com.gdn.android.onestop.ideation.util

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gdn.android.onestop.base.util.ItemClickCallback
import com.gdn.android.onestop.base.util.Util
import com.gdn.android.onestop.base.util.toAliasName
import com.gdn.android.onestop.base.util.toDateTime24String
import com.gdn.android.onestop.ideation.data.IdeaComment
import com.gdn.android.onestop.ideation.databinding.ItemCommentBinding
import java.util.*

class IdeaCommentRecyclerAdapter(
    private val profileClickCallback: ItemClickCallback<String>
) : RecyclerView.Adapter<IdeaCommentRecyclerAdapter.CommentViewHolder>(){

    private var commentList: List<IdeaComment> = LinkedList()

    fun updateData(commentList: List<IdeaComment>){
        this.commentList = commentList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return commentList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        return CommentViewHolder(
            ItemCommentBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val ideaComment = commentList[position]

        ideaComment.apply {
            holder.tvUsername.text = username
            holder.tvComment.text = text
            holder.tvDate.text = date.toDateTime24String()
            val nameAlias = ideaComment.username.toAliasName()
            holder.tvNamePict.text = nameAlias
            holder.tvNamePict.setBackgroundColor(Util.getColorFromString(ideaComment.username))

            holder.tvUsername.setOnClickListener { profileClickCallback.onItemClick(username, position) }
            holder.tvNamePict.setOnClickListener { profileClickCallback.onItemClick(username, position) }
        }
    }

    inner class CommentViewHolder(binding: ItemCommentBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvUsername : TextView = binding.tvUsername
        val tvDate : TextView = binding.tvDate
        val tvComment : TextView = binding.tvComment
        val tvNamePict : TextView = binding.ivUser
        init {
            tvComment.text = ""
        }
    }
}