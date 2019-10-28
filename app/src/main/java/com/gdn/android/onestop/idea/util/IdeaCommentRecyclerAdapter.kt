package com.gdn.android.onestop.idea.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gdn.android.onestop.R
import com.gdn.android.onestop.idea.data.IdeaComment

class IdeaCommentRecyclerAdapter :
    PagedListAdapter<IdeaComment, IdeaCommentRecyclerAdapter.CommentViewHolder>(
        COMMENT_COMPARATOR
    ){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val ideaComment = getItem(position)

        ideaComment?.let {
            holder.tvUsername.text = it.username
            holder.tvComment.text = it.text
            holder.tvDate.text = it.date
            Glide.with(holder.itemView)
                .load(R.drawable.ic_default_user)
                .into(holder.ivUser)
        }
    }

    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvUsername : TextView = itemView.findViewById(R.id.tv_username)
        val tvDate : TextView = itemView.findViewById(R.id.tv_date)
        val tvComment : TextView = itemView.findViewById(R.id.tv_comment)
        val ivUser : ImageView = itemView.findViewById(R.id.iv_user)
        init {
            tvComment.text = ""
        }
    }

    companion object {
        private val COMMENT_COMPARATOR = object : DiffUtil.ItemCallback<IdeaComment>() {
            override fun areItemsTheSame(oldItem: IdeaComment, newItem: IdeaComment): Boolean {
                return oldItem.hashCode() == newItem.hashCode()
            }

            override fun areContentsTheSame(oldItem: IdeaComment, newItem: IdeaComment): Boolean {
                return oldItem.hashCode() == newItem.hashCode()
            }

        }
    }
}