package com.gdn.android.onestop.ideation.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.gdn.android.onestop.base.FaSolidTextView
import com.gdn.android.onestop.base.util.ItemClickCallback
import com.gdn.android.onestop.base.util.Util
import com.gdn.android.onestop.base.util.toAliasName
import com.gdn.android.onestop.base.util.toDateString
import com.gdn.android.onestop.ideation.R
import com.gdn.android.onestop.ideation.data.IdeaPost
import java.util.*


class IdeaRecyclerAdapter(private val voteHelper: VoteHelper) :
    RecyclerView.Adapter<IdeaRecyclerAdapter.IdeaViewHolder>(
    ) {

    var ideaList: List<IdeaPost> = Collections.emptyList()

    override fun getItemCount(): Int {
        return ideaList.size
    }

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
        val ideaPost = ideaList[position]

        setVoteText(ideaPost, holder)

        holder.tvUsername.text = ideaPost.username
        holder.tvDate.text = ideaPost.createdAt.toDateString()

        holder.tvContent.text = ideaPost.content
        holder.tvContent.measure(0,0)

        if(holder.tvContent.lineCount < 4){
            holder.tvSeeMore.visibility = View.GONE
        }
        else{
            holder.tvSeeMore.visibility = View.VISIBLE
        }

        holder.tvComment.text = (holder.itemView.resources.getString(R.string.fa_comment) + " "+ ideaPost.commentCount)
        val nameAlias = ideaPost.username.toAliasName()
        holder.tvNamePict.text = nameAlias
        holder.tvNamePict.setBackgroundColor(Util.getColorFromString(ideaPost.username))

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


    inner class IdeaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvUsername: TextView = itemView.findViewById(R.id.tv_username)
        val tvDate: TextView = itemView.findViewById(R.id.tv_date)
        val tvContent: TextView = itemView.findViewById(R.id.tv_content)
        val tvNamePict: TextView = itemView.findViewById(R.id.tv_name_pict)
        val tvUpVote: FaSolidTextView = itemView.findViewById(R.id.tv_upVote)
        val tvDownVote: FaSolidTextView = itemView.findViewById(R.id.tv_downVote)
        val tvComment: FaSolidTextView = itemView.findViewById(R.id.tv_comment)
        val tvSeeMore: TextView = itemView.findViewById(R.id.tv_seemore)
    }

}