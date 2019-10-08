package com.gdn.android.onestop.idea

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
import com.gdn.android.onestop.base.AwesomeTextView
import com.gdn.android.onestop.base.ItemClickCallback
import com.gdn.android.onestop.base.VoteClickCallback
import com.gdn.android.onestop.idea.data.IdeaPost


class IdeaRecyclerViewAdapter :
    PagedListAdapter<IdeaPost,IdeaRecyclerViewAdapter.IdeaViewHolder>(IDEA_COMPARATOR) {

    lateinit var itemContentClickCallback : ItemClickCallback<IdeaPost>
    lateinit var voteClickCallback : VoteClickCallback


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IdeaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ideapost, parent, false)
        return IdeaViewHolder(view)
    }

    override fun onBindViewHolder(holder: IdeaViewHolder, position: Int) {
        val ideaPost = getItem(position)

        ideaPost?.let {
            holder.setVoteText(ideaPost)
            holder.tvUsername.text = ideaPost.username
            holder.tvDate.text = ideaPost.createdAt
            holder.tvContent.text = ideaPost.content
            Glide.with(holder.ivUserPict.context)
                .load(R.drawable.ic_iconfinder_male_628288)
                .into(holder.ivUserPict)

            this.itemContentClickCallback.let { itemClickCallback ->
                holder.tvContent.setOnClickListener {
                    itemClickCallback.onItemClick(ideaPost, position)
                }
                holder.tvComment.setOnClickListener{
                    itemClickCallback.onItemClick(ideaPost, position)
                }
            }

            this.voteClickCallback.let { voteClickCallback ->
                holder.tvUpVote.setOnClickListener {
                    voteClickCallback.onVote(ideaPost, holder, true)
                }
                holder.tvDownVote.setOnClickListener {
                    voteClickCallback.onVote(ideaPost, holder,false)
                }
            }


        }


    }


    inner class IdeaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvUsername = itemView.findViewById<TextView>(R.id.tv_username)
        val tvDate = itemView.findViewById<TextView>(R.id.tv_date)
        val tvContent = itemView.findViewById<TextView>(R.id.tv_content)
        val ivUserPict = itemView.findViewById<ImageView>(R.id.iv_user)
        val tvUpVote = itemView.findViewById<AwesomeTextView>(R.id.tv_upVote)
        val tvDownVote = itemView.findViewById<AwesomeTextView>(R.id.tv_downVote)
        val tvComment = itemView.findViewById<AwesomeTextView>(R.id.tv_comment)
    }



    companion object {
        private val IDEA_COMPARATOR = object : DiffUtil.ItemCallback<IdeaPost>(){
            override fun areItemsTheSame(oldItem: IdeaPost, newItem: IdeaPost): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: IdeaPost, newItem: IdeaPost): Boolean {
                return oldItem.id == newItem.id
            }

        }
    }

}