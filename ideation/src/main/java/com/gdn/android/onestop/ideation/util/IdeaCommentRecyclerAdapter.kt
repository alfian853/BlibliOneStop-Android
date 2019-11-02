package com.gdn.android.onestop.ideation.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.gdn.android.onestop.base.util.Util
import com.gdn.android.onestop.base.util.toAliasName
import com.gdn.android.onestop.ideation.R
import com.gdn.android.onestop.ideation.data.IdeaComment

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
            val nameAlias = ideaComment.username.toAliasName()
            holder.tvNamePict.text = nameAlias
            holder.tvNamePict.setBackgroundColor(Util.getColorFromString(nameAlias))
        }
    }

    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvUsername : TextView = itemView.findViewById(R.id.tv_username)
        val tvDate : TextView = itemView.findViewById(R.id.tv_date)
        val tvComment : TextView = itemView.findViewById(R.id.tv_comment)
        val tvNamePict : TextView = itemView.findViewById(R.id.iv_user)
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