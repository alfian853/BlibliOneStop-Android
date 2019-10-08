package com.gdn.android.onestop.base

import com.gdn.android.onestop.idea.IdeaRecyclerViewAdapter
import com.gdn.android.onestop.idea.data.IdeaPost

interface ItemClickCallback<T> {
    fun onItemClick(item : T, position : Int)
}

interface VoteClickCallback {
    fun onVote(ideaPost: IdeaPost, itemView : IdeaRecyclerViewAdapter.IdeaViewHolder, isVoteUp: Boolean)
}