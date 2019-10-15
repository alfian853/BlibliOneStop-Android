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
import com.gdn.android.onestop.base.FaSolidTextView
import com.gdn.android.onestop.util.ItemClickCallback
import com.gdn.android.onestop.util.VoteClickCallback
import com.gdn.android.onestop.idea.data.IdeaPost


class IdeaRecyclerAdapter(private val voteHelper: VoteHelper) :
    PagedListAdapter<IdeaPost,IdeaRecyclerAdapter.IdeaViewHolder>(IDEA_COMPARATOR) {

    lateinit var itemContentClickCallback : ItemClickCallback<IdeaPost>
    lateinit var voteClickCallback : VoteClickCallback


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IdeaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ideapost, parent, false)
        return IdeaViewHolder(view)
    }

    private fun setVoteText(ideaPost: IdeaPost, holder: IdeaViewHolder){
        voteHelper.setVoteText(
            holder.tvUpVote, holder.itemView.resources, true, ideaPost.upVoteCount, ideaPost.isMeVoteUp
        )

        voteHelper.setVoteText(
            holder.tvDownVote, holder.itemView.resources, false, ideaPost.downVoteCount, ideaPost.isMeVoteDown
        )
    }

    override fun onBindViewHolder(holder: IdeaViewHolder, position: Int) {
        val ideaPost = getItem(position)

        ideaPost?.let {

            setVoteText(ideaPost, holder)

            holder.tvUsername.text = ideaPost.username
            holder.tvDate.text = ideaPost.createdAt
            holder.tvContent.text = ideaPost.content
            Glide.with(holder.ivUserPict.context)
                .load(R.drawable.ic_default_user)
                .into(holder.ivUserPict)

            holder.tvComment.text = (holder.itemView.resources.getString(R.string.fa_comment) + " "+ ideaPost.commentCount)

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
        val tvUsername: TextView = itemView.findViewById(R.id.tv_username)
        val tvDate: TextView = itemView.findViewById(R.id.tv_date)
        val tvContent: TextView = itemView.findViewById(R.id.tv_content)
        val ivUserPict: ImageView = itemView.findViewById(R.id.iv_user)
        val tvUpVote: FaSolidTextView = itemView.findViewById(R.id.tv_upVote)
        val tvDownVote: FaSolidTextView = itemView.findViewById(R.id.tv_downVote)
        val tvComment: FaSolidTextView = itemView.findViewById(R.id.tv_comment)
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